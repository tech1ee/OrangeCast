package dev.orangecast.shared.data.processor

import dev.orangecast.shared.domain.model.AudioEnhancement
import dev.orangecast.shared.domain.model.AudioAnalysisProfile

expect class AudioProcessor {
    suspend fun applyEnhancement(enhancement: AudioEnhancement): Boolean
    suspend fun analyzeCurrentAudio(): AudioAnalysisProfile?
    suspend fun isEnhancementSupported(): Boolean
    suspend fun getProcessingLatency(): Long
    suspend fun getCpuUsage(): Float
    suspend fun optimizeForLowLatency(enabled: Boolean)
    suspend fun resetProcessor()
}