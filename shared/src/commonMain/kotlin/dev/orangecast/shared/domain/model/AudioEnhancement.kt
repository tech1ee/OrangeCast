package dev.orangecast.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AudioEnhancement(
    val volumeBoost: Float = 0f, // -20dB to +20dB
    val voiceBoost: Float = 0f,  // 0 to 100%
    val normalization: Boolean = false,
    val dynamicRangeCompression: Boolean = false,
    val noiseReduction: Float = 0f, // 0 to 100%
    val bassBoost: Float = 0f,      // 0 to 100% 
    val trebleBoost: Float = 0f     // 0 to 100%
) {
    companion object {
        fun createDefault(): AudioEnhancement {
            return AudioEnhancement()
        }

        fun createForVoiceContent(): AudioEnhancement {
            return AudioEnhancement(
                volumeBoost = 3f,
                voiceBoost = 25f,
                normalization = true,
                dynamicRangeCompression = true,
                noiseReduction = 15f,
                bassBoost = 0f,
                trebleBoost = 10f
            )
        }

        fun createForMusic(): AudioEnhancement {
            return AudioEnhancement(
                volumeBoost = 0f,
                voiceBoost = 0f,
                normalization = true,
                dynamicRangeCompression = false,
                noiseReduction = 5f,
                bassBoost = 15f,
                trebleBoost = 5f
            )
        }

        fun getPresets(): List<Pair<String, AudioEnhancement>> {
            return listOf(
                "Default" to createDefault(),
                "Voice Enhanced" to createForVoiceContent(),
                "Music Optimized" to createForMusic(),
                "Night Mode" to AudioEnhancement(
                    volumeBoost = 5f,
                    voiceBoost = 40f,
                    normalization = true,
                    dynamicRangeCompression = true,
                    noiseReduction = 25f,
                    bassBoost = -10f,
                    trebleBoost = 15f
                ),
                "Clarity Max" to AudioEnhancement(
                    volumeBoost = 6f,
                    voiceBoost = 50f,
                    normalization = true,
                    dynamicRangeCompression = true,
                    noiseReduction = 30f,
                    bassBoost = -15f,
                    trebleBoost = 25f
                )
            )
        }
    }

    fun isEnhanced(): Boolean {
        return volumeBoost != 0f ||
               voiceBoost != 0f ||
               normalization ||
               dynamicRangeCompression ||
               noiseReduction != 0f ||
               bassBoost != 0f ||
               trebleBoost != 0f
    }

    fun getVolumeMultiplier(): Float {
        return kotlin.math.pow(10.0, (volumeBoost / 20.0)).toFloat().coerceIn(0.1f, 10f)
    }

    fun validate(): AudioEnhancement {
        return copy(
            volumeBoost = volumeBoost.coerceIn(-20f, 20f),
            voiceBoost = voiceBoost.coerceIn(0f, 100f),
            noiseReduction = noiseReduction.coerceIn(0f, 100f),
            bassBoost = bassBoost.coerceIn(-30f, 30f),
            trebleBoost = trebleBoost.coerceIn(-30f, 30f)
        )
    }
}

@Serializable
data class AudioProcessingState(
    val isProcessing: Boolean = false,
    val currentEnhancement: AudioEnhancement = AudioEnhancement.createDefault(),
    val processingLatencyMs: Long = 0L,
    val cpuUsagePercent: Float = 0f
)

enum class AudioContentType {
    VOICE_ONLY,
    MUSIC_ONLY,
    MIXED_CONTENT,
    UNKNOWN
}

data class AudioAnalysisProfile(
    val contentType: AudioContentType,
    val averageVolumeDb: Float,
    val dynamicRange: Float,
    val voiceFrequencyRatio: Float,
    val musicFrequencyRatio: Float,
    val noiseFloor: Float,
    val recommendedEnhancement: AudioEnhancement
) {
    fun getSuitabilityScore(enhancement: AudioEnhancement): Float {
        var score = 1.0f
        
        when (contentType) {
            AudioContentType.VOICE_ONLY -> {
                if (enhancement.voiceBoost > 0) score += 0.3f
                if (enhancement.bassBoost < 0) score += 0.2f
                if (enhancement.noiseReduction > 0) score += 0.2f
            }
            AudioContentType.MUSIC_ONLY -> {
                if (enhancement.bassBoost > 0) score += 0.2f
                if (enhancement.trebleBoost > 0) score += 0.1f
                if (!enhancement.dynamicRangeCompression) score += 0.2f
            }
            AudioContentType.MIXED_CONTENT -> {
                if (enhancement.normalization) score += 0.2f
                if (enhancement.voiceBoost in 10f..30f) score += 0.1f
            }
            AudioContentType.UNKNOWN -> {
                // Neutral scoring for unknown content
            }
        }
        
        // Penalty for extreme settings
        if (enhancement.volumeBoost > 15f) score -= 0.3f
        if (enhancement.voiceBoost > 80f) score -= 0.2f
        
        return score.coerceIn(0f, 2f)
    }
}