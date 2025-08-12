package dev.orangecast.shared.domain.model

data class SilenceDetectionConfig(
    val aggressiveness: SilenceAggressiveness,
    val silenceThresholdDb: Float,
    val minimumSilenceDurationMs: Long,
    val minimumNonSilenceDurationMs: Long,
    val maxSkipDurationMs: Long
) {
    companion object {
        fun createConfig(aggressiveness: SilenceAggressiveness): SilenceDetectionConfig {
            return when (aggressiveness) {
                SilenceAggressiveness.MILD -> SilenceDetectionConfig(
                    aggressiveness = aggressiveness,
                    silenceThresholdDb = -40.0f,
                    minimumSilenceDurationMs = 2000L,
                    minimumNonSilenceDurationMs = 1000L,
                    maxSkipDurationMs = 5000L
                )
                SilenceAggressiveness.MEDIUM -> SilenceDetectionConfig(
                    aggressiveness = aggressiveness,
                    silenceThresholdDb = -35.0f,
                    minimumSilenceDurationMs = 1500L,
                    minimumNonSilenceDurationMs = 800L,
                    maxSkipDurationMs = 8000L
                )
                SilenceAggressiveness.AGGRESSIVE -> SilenceDetectionConfig(
                    aggressiveness = aggressiveness,
                    silenceThresholdDb = -30.0f,
                    minimumSilenceDurationMs = 1000L,
                    minimumNonSilenceDurationMs = 500L,
                    maxSkipDurationMs = 12000L
                )
            }
        }
    }
}

enum class SilenceAggressiveness(val displayName: String) {
    MILD("Mild"),
    MEDIUM("Medium"), 
    AGGRESSIVE("Mad Max")
}

data class SilenceSegment(
    val startTimeMs: Long,
    val endTimeMs: Long,
    val averageVolumeDb: Float,
    val confidenceScore: Float
) {
    val durationMs: Long = endTimeMs - startTimeMs
    
    fun shouldSkip(config: SilenceDetectionConfig): Boolean {
        return durationMs >= config.minimumSilenceDurationMs &&
               durationMs <= config.maxSkipDurationMs &&
               averageVolumeDb <= config.silenceThresholdDb &&
               confidenceScore >= 0.7f
    }
}

data class AudioAnalysisResult(
    val silenceSegments: List<SilenceSegment>,
    val totalSilenceDurationMs: Long,
    val averageVolumeDb: Float,
    val processingTimeMs: Long
) {
    val totalSkippableSilenceMs: Long
        get() = silenceSegments.sumOf { it.durationMs }
    
    fun getSkippableSegments(config: SilenceDetectionConfig): List<SilenceSegment> {
        return silenceSegments.filter { it.shouldSkip(config) }
    }
}