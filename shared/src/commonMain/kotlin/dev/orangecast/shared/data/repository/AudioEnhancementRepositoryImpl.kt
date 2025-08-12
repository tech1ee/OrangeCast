package dev.orangecast.shared.data.repository

import dev.orangecast.shared.domain.model.AudioEnhancement
import dev.orangecast.shared.domain.model.AudioProcessingState
import dev.orangecast.shared.domain.model.AudioAnalysisProfile
import dev.orangecast.shared.domain.repository.AudioEnhancementRepository
import dev.orangecast.shared.data.processor.AudioProcessor
import dev.orangecast.shared.data.storage.PreferencesStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioEnhancementRepositoryImpl(
    private val audioProcessor: AudioProcessor,
    private val preferencesStorage: PreferencesStorage
) : AudioEnhancementRepository {
    
    private val _processingState = MutableStateFlow(AudioProcessingState())
    private var currentEnhancement = AudioEnhancement.createDefault()
    
    override suspend fun applyEnhancement(enhancement: AudioEnhancement): Boolean {
        return try {
            _processingState.value = _processingState.value.copy(isProcessing = true)
            
            val validatedEnhancement = enhancement.validate()
            val success = audioProcessor.applyEnhancement(validatedEnhancement)
            
            if (success) {
                currentEnhancement = validatedEnhancement
                preferencesStorage.saveCurrentEnhancement(validatedEnhancement)
                
                _processingState.value = _processingState.value.copy(
                    isProcessing = false,
                    currentEnhancement = validatedEnhancement,
                    processingLatencyMs = audioProcessor.getProcessingLatency(),
                    cpuUsagePercent = audioProcessor.getCpuUsage()
                )
            } else {
                _processingState.value = _processingState.value.copy(isProcessing = false)
            }
            
            success
        } catch (e: Exception) {
            _processingState.value = _processingState.value.copy(isProcessing = false)
            false
        }
    }
    
    override suspend fun getCurrentEnhancement(): AudioEnhancement {
        if (currentEnhancement == AudioEnhancement.createDefault()) {
            currentEnhancement = preferencesStorage.getCurrentEnhancement() ?: AudioEnhancement.createDefault()
        }
        return currentEnhancement
    }
    
    override suspend fun resetEnhancement() {
        val defaultEnhancement = AudioEnhancement.createDefault()
        applyEnhancement(defaultEnhancement)
    }
    
    override suspend fun saveUserPreset(name: String, enhancement: AudioEnhancement) {
        val validatedEnhancement = enhancement.validate()
        preferencesStorage.saveUserPreset(name, validatedEnhancement)
    }
    
    override suspend fun getUserPresets(): Map<String, AudioEnhancement> {
        return preferencesStorage.getUserPresets()
    }
    
    override suspend fun deleteUserPreset(name: String) {
        preferencesStorage.deleteUserPreset(name)
    }
    
    override fun observeProcessingState(): Flow<AudioProcessingState> {
        return _processingState.asStateFlow()
    }
    
    override suspend fun analyzeCurrentAudio(): AudioAnalysisProfile? {
        return audioProcessor.analyzeCurrentAudio()
    }
    
    override suspend fun isEnhancementSupported(): Boolean {
        return audioProcessor.isEnhancementSupported()
    }
    
    override suspend fun getProcessingLatency(): Long {
        return audioProcessor.getProcessingLatency()
    }
    
    override suspend fun optimizeForLowLatency(enabled: Boolean) {
        audioProcessor.optimizeForLowLatency(enabled)
    }
}