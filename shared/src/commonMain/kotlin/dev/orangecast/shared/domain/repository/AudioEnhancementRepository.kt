package dev.orangecast.shared.domain.repository

import dev.orangecast.shared.domain.model.AudioEnhancement
import dev.orangecast.shared.domain.model.AudioProcessingState
import dev.orangecast.shared.domain.model.AudioAnalysisProfile
import kotlinx.coroutines.flow.Flow

interface AudioEnhancementRepository {
    suspend fun applyEnhancement(enhancement: AudioEnhancement): Boolean
    suspend fun getCurrentEnhancement(): AudioEnhancement
    suspend fun resetEnhancement()
    suspend fun saveUserPreset(name: String, enhancement: AudioEnhancement)
    suspend fun getUserPresets(): Map<String, AudioEnhancement>
    suspend fun deleteUserPreset(name: String)
    fun observeProcessingState(): Flow<AudioProcessingState>
    suspend fun analyzeCurrentAudio(): AudioAnalysisProfile?
    suspend fun isEnhancementSupported(): Boolean
    suspend fun getProcessingLatency(): Long
    suspend fun optimizeForLowLatency(enabled: Boolean)
}