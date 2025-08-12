package dev.orangecast.shared.data.processor

import android.media.audiofx.*
import androidx.media3.exoplayer.audio.AudioProcessor
import androidx.media3.exoplayer.audio.AudioProcessorChain
import dev.orangecast.shared.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.pow

actual class AudioProcessor {
    
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var presetReverb: PresetReverb? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null
    private var noiseSuppressor: NoiseSuppressor? = null
    private var audioSessionId: Int = 0
    private var isLowLatencyMode = false
    
    actual suspend fun applyEnhancement(enhancement: AudioEnhancement): Boolean {
        return withContext(Dispatchers.Default) {
            try {
                initializeEffects()
                
                // Apply volume boost through LoudnessEnhancer
                loudnessEnhancer?.let { enhancer ->
                    if (enhancement.volumeBoost != 0f) {
                        enhancer.enabled = true
                        val gainMb = (enhancement.volumeBoost * 100).toInt()
                        enhancer.setTargetGain(gainMb.coerceIn(-2000, 2000))
                    } else {
                        enhancer.enabled = false
                    }
                }
                
                // Apply bass boost
                bassBoost?.let { bass ->
                    if (enhancement.bassBoost != 0f) {
                        bass.enabled = true
                        val strength = (enhancement.bassBoost * 10).toInt()
                        bass.setStrength(strength.toShort().coerceIn(0, 1000))
                    } else {
                        bass.enabled = false
                    }
                }
                
                // Apply treble boost through equalizer
                equalizer?.let { eq ->
                    if (enhancement.trebleBoost != 0f || enhancement.voiceBoost != 0f) {
                        eq.enabled = true
                        applyEqualizerSettings(eq, enhancement)
                    } else {
                        eq.enabled = false
                    }
                }
                
                // Apply noise reduction
                noiseSuppressor?.let { suppressor ->
                    suppressor.enabled = enhancement.noiseReduction > 0f
                }
                
                // Apply dynamic range compression through preset reverb
                presetReverb?.let { reverb ->
                    if (enhancement.dynamicRangeCompression) {
                        reverb.enabled = true
                        reverb.preset = PresetReverb.PRESET_SMALLROOM
                    } else {
                        reverb.enabled = false
                    }
                }
                
                true
                
            } catch (e: Exception) {
                false
            }
        }
    }
    
    actual suspend fun analyzeCurrentAudio(): AudioAnalysisProfile? {
        return withContext(Dispatchers.Default) {
            try {
                // Analyze current audio characteristics
                val averageVolumeDb = measureAverageVolume()
                val dynamicRange = measureDynamicRange()
                val frequencyAnalysis = analyzeFrequencyContent()
                
                val contentType = when {
                    frequencyAnalysis.voiceRatio > 0.7f -> AudioContentType.VOICE_ONLY
                    frequencyAnalysis.musicRatio > 0.8f -> AudioContentType.MUSIC_ONLY
                    frequencyAnalysis.voiceRatio > 0.3f -> AudioContentType.MIXED_CONTENT
                    else -> AudioContentType.UNKNOWN
                }
                
                val recommendedEnhancement = when (contentType) {
                    AudioContentType.VOICE_ONLY -> AudioEnhancement.createForVoiceContent()
                    AudioContentType.MUSIC_ONLY -> AudioEnhancement.createForMusic()
                    else -> AudioEnhancement.createDefault()
                }
                
                AudioAnalysisProfile(
                    contentType = contentType,
                    averageVolumeDb = averageVolumeDb,
                    dynamicRange = dynamicRange,
                    voiceFrequencyRatio = frequencyAnalysis.voiceRatio,
                    musicFrequencyRatio = frequencyAnalysis.musicRatio,
                    noiseFloor = frequencyAnalysis.noiseFloor,
                    recommendedEnhancement = recommendedEnhancement
                )
                
            } catch (e: Exception) {
                null
            }
        }
    }
    
    actual suspend fun isEnhancementSupported(): Boolean {
        return try {
            Equalizer.isAvailable() && BassBoost.isAvailable() && LoudnessEnhancer.isAvailable()
        } catch (e: Exception) {
            false
        }
    }
    
    actual suspend fun getProcessingLatency(): Long {
        return if (isLowLatencyMode) 10L else 50L
    }
    
    actual suspend fun getCpuUsage(): Float {
        return when {
            equalizer?.enabled == true || bassBoost?.enabled == true -> 15f
            loudnessEnhancer?.enabled == true -> 10f
            else -> 5f
        }
    }
    
    actual suspend fun optimizeForLowLatency(enabled: Boolean) {
        isLowLatencyMode = enabled
        
        if (enabled) {
            equalizer?.enabled = false
            presetReverb?.enabled = false
        }
    }
    
    actual suspend fun resetProcessor() {
        releaseEffects()
    }
    
    fun setAudioSessionId(sessionId: Int) {
        this.audioSessionId = sessionId
        releaseEffects()
        initializeEffects()
    }
    
    private fun initializeEffects() {
        try {
            if (audioSessionId != 0) {
                equalizer = Equalizer(0, audioSessionId)
                bassBoost = BassBoost(0, audioSessionId)
                virtualizer = Virtualizer(0, audioSessionId)
                presetReverb = PresetReverb(0, audioSessionId)
                
                if (LoudnessEnhancer.isAvailable()) {
                    loudnessEnhancer = LoudnessEnhancer(audioSessionId)
                }
                
                if (NoiseSuppressor.isAvailable()) {
                    noiseSuppressor = NoiseSuppressor.create(audioSessionId)
                }
            }
        } catch (e: Exception) {
            // Effects initialization failed
        }
    }
    
    private fun releaseEffects() {
        equalizer?.release()
        bassBoost?.release()
        virtualizer?.release()
        presetReverb?.release()
        loudnessEnhancer?.release()
        noiseSuppressor?.release()
        
        equalizer = null
        bassBoost = null
        virtualizer = null
        presetReverb = null
        loudnessEnhancer = null
        noiseSuppressor = null
    }
    
    private fun applyEqualizerSettings(eq: Equalizer, enhancement: AudioEnhancement) {
        try {
            val numberOfBands = eq.numberOfBands
            val bandLevelRange = eq.bandLevelRange
            
            for (band in 0 until numberOfBands) {
                val centerFreq = eq.getCenterFreq(band.toShort())
                
                val gain = when {
                    centerFreq < 250 -> {
                        // Bass frequencies - affected by bassBoost handled separately
                        0
                    }
                    centerFreq in 250..2000 -> {
                        // Voice frequencies
                        (enhancement.voiceBoost * bandLevelRange[1] / 100f).toInt()
                    }
                    centerFreq > 4000 -> {
                        // Treble frequencies
                        (enhancement.trebleBoost * bandLevelRange[1] / 100f).toInt()
                    }
                    else -> 0
                }
                
                eq.setBandLevel(band.toShort(), gain.toShort())
            }
        } catch (e: Exception) {
            // Equalizer setup failed
        }
    }
    
    private fun measureAverageVolume(): Float {
        return -20f // Placeholder implementation
    }
    
    private fun measureDynamicRange(): Float {
        return 30f // Placeholder implementation
    }
    
    private fun analyzeFrequencyContent(): FrequencyAnalysis {
        return FrequencyAnalysis(
            voiceRatio = 0.6f,
            musicRatio = 0.4f,
            noiseFloor = -60f
        )
    }
    
    private data class FrequencyAnalysis(
        val voiceRatio: Float,
        val musicRatio: Float,
        val noiseFloor: Float
    )
}