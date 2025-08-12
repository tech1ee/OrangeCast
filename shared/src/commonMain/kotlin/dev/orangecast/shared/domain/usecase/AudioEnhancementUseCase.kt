package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.AudioEnhancement
import dev.orangecast.shared.domain.model.AudioProcessingState
import dev.orangecast.shared.domain.model.AudioAnalysisProfile
import dev.orangecast.shared.domain.repository.AudioEnhancementRepository
import kotlinx.coroutines.flow.Flow

class AudioEnhancementUseCase(
    private val audioEnhancementRepository: AudioEnhancementRepository
) {
    
    suspend fun applyEnhancement(enhancement: AudioEnhancement): Boolean {
        val validatedEnhancement = enhancement.validate()
        return audioEnhancementRepository.applyEnhancement(validatedEnhancement)
    }
    
    suspend fun applyPreset(presetName: String): Boolean {
        val preset = getPresetByName(presetName)
        return if (preset != null) {
            applyEnhancement(preset)
        } else false
    }
    
    suspend fun getCurrentEnhancement(): AudioEnhancement {
        return audioEnhancementRepository.getCurrentEnhancement()
    }
    
    suspend fun resetToDefault(): Boolean {
        return applyEnhancement(AudioEnhancement.createDefault())
    }
    
    suspend fun saveCustomPreset(name: String, enhancement: AudioEnhancement): Boolean {
        return try {
            audioEnhancementRepository.saveUserPreset(name, enhancement.validate())
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun deleteCustomPreset(name: String): Boolean {
        return try {
            audioEnhancementRepository.deleteUserPreset(name)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun getAllPresets(): Map<String, AudioEnhancement> {
        val builtInPresets = AudioEnhancement.getPresets().toMap()
        val userPresets = audioEnhancementRepository.getUserPresets()
        return builtInPresets + userPresets
    }
    
    suspend fun getPresetByName(name: String): AudioEnhancement? {
        val allPresets = getAllPresets()
        return allPresets[name]
    }
    
    suspend fun analyzeAndRecommend(): AudioEnhancement? {
        val profile = audioEnhancementRepository.analyzeCurrentAudio()
        return profile?.recommendedEnhancement
    }
    
    suspend fun getOptimalEnhancement(): AudioEnhancement {
        val recommendation = analyzeAndRecommend()
        return recommendation ?: AudioEnhancement.createDefault()
    }
    
    suspend fun isEnhancementSupported(): Boolean {
        return audioEnhancementRepository.isEnhancementSupported()
    }
    
    fun observeProcessingState(): Flow<AudioProcessingState> {
        return audioEnhancementRepository.observeProcessingState()
    }
    
    suspend fun getProcessingLatency(): Long {
        return audioEnhancementRepository.getProcessingLatency()
    }
    
    suspend fun optimizeForPerformance(lowLatency: Boolean = true) {
        audioEnhancementRepository.optimizeForLowLatency(lowLatency)
    }
    
    suspend fun createCustomEnhancement(
        volumeBoost: Float = 0f,
        voiceBoost: Float = 0f,
        bassBoost: Float = 0f,
        trebleBoost: Float = 0f,
        enableNormalization: Boolean = false,
        enableCompression: Boolean = false,
        noiseReduction: Float = 0f
    ): AudioEnhancement {
        return AudioEnhancement(
            volumeBoost = volumeBoost,
            voiceBoost = voiceBoost,
            bassBoost = bassBoost,
            trebleBoost = trebleBoost,
            normalization = enableNormalization,
            dynamicRangeCompression = enableCompression,
            noiseReduction = noiseReduction
        ).validate()
    }
    
    suspend fun adjustVolumeBoost(currentEnhancement: AudioEnhancement, delta: Float): AudioEnhancement {
        val newVolumeBoost = (currentEnhancement.volumeBoost + delta).coerceIn(-20f, 20f)
        return currentEnhancement.copy(volumeBoost = newVolumeBoost)
    }
    
    suspend fun adjustVoiceBoost(currentEnhancement: AudioEnhancement, delta: Float): AudioEnhancement {
        val newVoiceBoost = (currentEnhancement.voiceBoost + delta).coerceIn(0f, 100f)
        return currentEnhancement.copy(voiceBoost = newVoiceBoost)
    }
    
    suspend fun adjustBassBoost(currentEnhancement: AudioEnhancement, delta: Float): AudioEnhancement {
        val newBassBoost = (currentEnhancement.bassBoost + delta).coerceIn(-30f, 30f)
        return currentEnhancement.copy(bassBoost = newBassBoost)
    }
    
    suspend fun adjustTrebleBoost(currentEnhancement: AudioEnhancement, delta: Float): AudioEnhancement {
        val newTrebleBoost = (currentEnhancement.trebleBoost + delta).coerceIn(-30f, 30f)
        return currentEnhancement.copy(trebleBoost = newTrebleBoost)
    }
    
    suspend fun toggleNormalization(currentEnhancement: AudioEnhancement): AudioEnhancement {
        return currentEnhancement.copy(normalization = !currentEnhancement.normalization)
    }
    
    suspend fun toggleDynamicRangeCompression(currentEnhancement: AudioEnhancement): AudioEnhancement {
        return currentEnhancement.copy(dynamicRangeCompression = !currentEnhancement.dynamicRangeCompression)
    }
    
    suspend fun adjustNoiseReduction(currentEnhancement: AudioEnhancement, delta: Float): AudioEnhancement {
        val newNoiseReduction = (currentEnhancement.noiseReduction + delta).coerceIn(0f, 100f)
        return currentEnhancement.copy(noiseReduction = newNoiseReduction)
    }
    
    suspend fun getEnhancementSuitabilityScore(enhancement: AudioEnhancement): Float {
        val profile = audioEnhancementRepository.analyzeCurrentAudio()
        return profile?.getSuitabilityScore(enhancement) ?: 1.0f
    }
    
    suspend fun findBestPresetForCurrentAudio(): Pair<String, AudioEnhancement>? {
        val allPresets = getAllPresets()
        val profile = audioEnhancementRepository.analyzeCurrentAudio() ?: return null
        
        return allPresets.maxByOrNull { (_, enhancement) ->
            profile.getSuitabilityScore(enhancement)
        }?.let { (name, enhancement) ->
            name to enhancement
        }
    }
    
    suspend fun compareEnhancements(enhancement1: AudioEnhancement, enhancement2: AudioEnhancement): String {
        val score1 = getEnhancementSuitabilityScore(enhancement1)
        val score2 = getEnhancementSuitabilityScore(enhancement2)
        
        return when {
            score1 > score2 -> "First enhancement is better suited for current audio"
            score2 > score1 -> "Second enhancement is better suited for current audio"
            else -> "Both enhancements are equally suited for current audio"
        }
    }
}