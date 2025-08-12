package dev.orangecast.shared.integration

import dev.orangecast.shared.domain.model.*
import dev.orangecast.shared.domain.usecase.AudioEnhancementUseCase
import dev.orangecast.shared.data.repository.AudioEnhancementRepositoryImpl
import dev.orangecast.shared.data.processor.AudioProcessor
import dev.orangecast.shared.data.storage.PreferencesStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class AudioEnhancementIntegrationTest {
    
    private class IntegrationTestAudioProcessor : AudioProcessor {
        private var appliedEnhancement = AudioEnhancement.createDefault()
        var processingHistory = mutableListOf<AudioEnhancement>()
        var isSupported = true
        var latency = 15L
        var lowLatencyOptimized = false
        
        override suspend fun applyEnhancement(enhancement: AudioEnhancement): Boolean {
            processingHistory.add(enhancement)
            appliedEnhancement = enhancement
            return true
        }
        
        override suspend fun analyzeCurrentAudio(): AudioAnalysisProfile {
            return when {
                appliedEnhancement.voiceBoost > 20f -> AudioAnalysisProfile(
                    contentType = AudioContentType.VOICE_ONLY,
                    averageVolumeDb = -18f,
                    dynamicRange = 15f,
                    voiceFrequencyRatio = 0.85f,
                    musicFrequencyRatio = 0.15f,
                    noiseFloor = -60f,
                    recommendedEnhancement = AudioEnhancement.createForVoiceContent()
                )
                appliedEnhancement.bassBoost > 10f -> AudioAnalysisProfile(
                    contentType = AudioContentType.MUSIC_ONLY,
                    averageVolumeDb = -12f,
                    dynamicRange = 35f,
                    voiceFrequencyRatio = 0.2f,
                    musicFrequencyRatio = 0.8f,
                    noiseFloor = -65f,
                    recommendedEnhancement = AudioEnhancement.createForMusic()
                )
                else -> AudioAnalysisProfile(
                    contentType = AudioContentType.MIXED_CONTENT,
                    averageVolumeDb = -16f,
                    dynamicRange = 25f,
                    voiceFrequencyRatio = 0.5f,
                    musicFrequencyRatio = 0.5f,
                    noiseFloor = -62f,
                    recommendedEnhancement = AudioEnhancement.createDefault()
                )
            }
        }
        
        override suspend fun isEnhancementSupported(): Boolean = isSupported
        override suspend fun getProcessingLatency(): Long = if (lowLatencyOptimized) 5L else latency
        override suspend fun getCpuUsage(): Float = when {
            appliedEnhancement.isEnhanced() -> 25f
            else -> 8f
        }
        
        override suspend fun optimizeForLowLatency(enabled: Boolean) {
            lowLatencyOptimized = enabled
        }
        
        override suspend fun resetProcessor() {
            appliedEnhancement = AudioEnhancement.createDefault()
            processingHistory.clear()
        }
    }
    
    private class IntegrationTestPreferencesStorage : PreferencesStorage {
        private val storage = mutableMapOf<String, Any>()
        
        override suspend fun saveCurrentEnhancement(enhancement: AudioEnhancement) {
            storage["current"] = enhancement
        }
        
        override suspend fun getCurrentEnhancement(): AudioEnhancement? {
            return storage["current"] as? AudioEnhancement
        }
        
        override suspend fun saveUserPreset(name: String, enhancement: AudioEnhancement) {
            val presets = (storage["presets"] as? MutableMap<String, AudioEnhancement>) ?: mutableMapOf()
            presets[name] = enhancement
            storage["presets"] = presets
        }
        
        override suspend fun getUserPresets(): Map<String, AudioEnhancement> {
            return (storage["presets"] as? Map<String, AudioEnhancement>) ?: emptyMap()
        }
        
        override suspend fun deleteUserPreset(name: String) {
            val presets = (storage["presets"] as? MutableMap<String, AudioEnhancement>) ?: return
            presets.remove(name)
        }
        
        override suspend fun clearAllPresets() {
            storage.clear()
        }
    }
    
    private lateinit var audioProcessor: IntegrationTestAudioProcessor
    private lateinit var preferencesStorage: IntegrationTestPreferencesStorage
    private lateinit var repository: AudioEnhancementRepositoryImpl
    private lateinit var useCase: AudioEnhancementUseCase
    
    @BeforeTest
    fun setup() {
        audioProcessor = IntegrationTestAudioProcessor()
        preferencesStorage = IntegrationTestPreferencesStorage()
        repository = AudioEnhancementRepositoryImpl(audioProcessor, preferencesStorage)
        useCase = AudioEnhancementUseCase(repository)
    }
    
    @Test
    fun completeWorkflow_voiceContentOptimization() = runTest {
        val voiceEnhancement = AudioEnhancement.createForVoiceContent()
        
        val applyResult = useCase.applyEnhancement(voiceEnhancement)
        assertTrue(applyResult, "Voice enhancement should be applied successfully")
        
        val currentEnhancement = useCase.getCurrentEnhancement()
        assertEquals(voiceEnhancement, currentEnhancement, "Current enhancement should match applied enhancement")
        
        val processingState = useCase.observeProcessingState().first()
        assertEquals(voiceEnhancement, processingState.currentEnhancement, "Processing state should reflect current enhancement")
        assertFalse(processingState.isProcessing, "Processing should be complete")
        assertTrue(processingState.cpuUsagePercent > 20f, "Enhanced processing should increase CPU usage")
        
        val analysis = repository.analyzeCurrentAudio()
        assertNotNull(analysis, "Audio analysis should be available")
        assertEquals(AudioContentType.VOICE_ONLY, analysis.contentType, "Analysis should detect voice content")
        assertTrue(analysis.voiceFrequencyRatio > 0.8f, "Voice frequency ratio should be high")
        
        val savedEnhancement = preferencesStorage.getCurrentEnhancement()
        assertEquals(voiceEnhancement, savedEnhancement, "Enhancement should be persisted")
    }
    
    @Test
    fun completeWorkflow_customPresetManagement() = runTest {
        val customEnhancement = useCase.createCustomEnhancement(
            volumeBoost = 8f,
            voiceBoost = 35f,
            bassBoost = 5f,
            trebleBoost = 12f,
            enableNormalization = true,
            noiseReduction = 20f
        )
        
        useCase.applyEnhancement(customEnhancement)
        
        val saveResult = useCase.saveCustomPreset("My Custom Preset", customEnhancement)
        assertTrue(saveResult, "Custom preset should be saved successfully")
        
        useCase.resetToDefault()
        assertEquals(AudioEnhancement.createDefault(), useCase.getCurrentEnhancement(), "Should reset to default")
        
        val applyPresetResult = useCase.applyPreset("My Custom Preset")
        assertTrue(applyPresetResult, "Custom preset should be applied successfully")
        assertEquals(customEnhancement, useCase.getCurrentEnhancement(), "Custom preset should be restored")
        
        val allPresets = useCase.getAllPresets()
        assertTrue(allPresets.containsKey("My Custom Preset"), "All presets should include custom preset")
        assertTrue(allPresets.size > 5, "Should include both built-in and custom presets")
        
        val deleteResult = useCase.deleteCustomPreset("My Custom Preset")
        assertTrue(deleteResult, "Custom preset should be deleted successfully")
        
        val presetsAfterDeletion = useCase.getAllPresets()
        assertFalse(presetsAfterDeletion.containsKey("My Custom Preset"), "Deleted preset should not be in list")
    }
    
    @Test
    fun completeWorkflow_adaptiveEnhancementRecommendations() = runTest {
        useCase.applyEnhancement(AudioEnhancement.createForMusic())
        
        val recommendation = useCase.analyzeAndRecommend()
        assertNotNull(recommendation, "Should provide recommendation based on current audio")
        assertEquals(AudioEnhancement.createForMusic(), recommendation, "Should recommend music enhancement for music content")
        
        val bestPreset = useCase.findBestPresetForCurrentAudio()
        assertNotNull(bestPreset, "Should find best preset for current audio")
        assertEquals("Music Optimized", bestPreset.first, "Should recommend music preset")
        
        val suitabilityScore = useCase.getEnhancementSuitabilityScore(AudioEnhancement.createForMusic())
        assertTrue(suitabilityScore > 1.0f, "Music enhancement should score well for music content")
        
        val voiceScore = useCase.getEnhancementSuitabilityScore(AudioEnhancement.createForVoiceContent())
        assertTrue(suitabilityScore > voiceScore, "Music enhancement should score better than voice for music content")
    }
    
    @Test
    fun completeWorkflow_performanceOptimization() = runTest {
        val initialLatency = useCase.getProcessingLatency()
        assertTrue(initialLatency > 10L, "Initial latency should be reasonable")
        
        useCase.optimizeForPerformance(true)
        assertTrue(audioProcessor.lowLatencyOptimized, "Low latency optimization should be enabled")
        
        val optimizedLatency = useCase.getProcessingLatency()
        assertTrue(optimizedLatency < initialLatency, "Optimized latency should be lower")
        
        val enhancement = AudioEnhancement(
            volumeBoost = 10f,
            voiceBoost = 50f,
            bassBoost = 15f,
            noiseReduction = 30f,
            normalization = true,
            dynamicRangeCompression = true
        )
        
        val applyResult = useCase.applyEnhancement(enhancement)
        assertTrue(applyResult, "Complex enhancement should be applied even in low latency mode")
        
        val processingState = useCase.observeProcessingState().first()
        assertTrue(processingState.processingLatencyMs < 10L, "Processing latency should be optimized")
    }
    
    @Test
    fun completeWorkflow_enhancementValidationAndConstraints() = runTest {
        val extremeEnhancement = AudioEnhancement(
            volumeBoost = 100f,
            voiceBoost = 200f,
            bassBoost = -100f,
            trebleBoost = 50f,
            noiseReduction = -50f
        )
        
        val applyResult = useCase.applyEnhancement(extremeEnhancement)
        assertTrue(applyResult, "Should handle extreme values gracefully")
        
        val appliedEnhancement = useCase.getCurrentEnhancement()
        assertTrue(appliedEnhancement.volumeBoost <= 20f, "Volume boost should be constrained")
        assertTrue(appliedEnhancement.voiceBoost <= 100f, "Voice boost should be constrained")
        assertTrue(appliedEnhancement.bassBoost >= -30f, "Bass boost should be constrained")
        assertTrue(appliedEnhancement.trebleBoost <= 30f, "Treble boost should be constrained")
        assertTrue(appliedEnhancement.noiseReduction >= 0f, "Noise reduction should be constrained")
        
        assertTrue(audioProcessor.processingHistory.isNotEmpty(), "Processing history should record attempts")
        
        val validatedValues = audioProcessor.processingHistory.last()
        assertEquals(20f, validatedValues.volumeBoost, "Processor should receive validated values")
        assertEquals(100f, validatedValues.voiceBoost, "Processor should receive validated values")
    }
    
    @Test
    fun completeWorkflow_enhancementAdjustments() = runTest {
        var currentEnhancement = AudioEnhancement.createDefault()
        useCase.applyEnhancement(currentEnhancement)
        
        currentEnhancement = useCase.adjustVolumeBoost(currentEnhancement, 5f)
        useCase.applyEnhancement(currentEnhancement)
        assertEquals(5f, useCase.getCurrentEnhancement().volumeBoost, "Volume boost should be adjusted")
        
        currentEnhancement = useCase.adjustVoiceBoost(currentEnhancement, 30f)
        useCase.applyEnhancement(currentEnhancement)
        assertEquals(30f, useCase.getCurrentEnhancement().voiceBoost, "Voice boost should be adjusted")
        
        currentEnhancement = useCase.toggleNormalization(currentEnhancement)
        useCase.applyEnhancement(currentEnhancement)
        assertTrue(useCase.getCurrentEnhancement().normalization, "Normalization should be toggled")
        
        currentEnhancement = useCase.toggleDynamicRangeCompression(currentEnhancement)
        useCase.applyEnhancement(currentEnhancement)
        assertTrue(useCase.getCurrentEnhancement().dynamicRangeCompression, "Compression should be toggled")
        
        assertTrue(useCase.getCurrentEnhancement().isEnhanced(), "Final enhancement should be detected as enhanced")
    }
    
    @Test
    fun completeWorkflow_enhancementComparison() = runTest {
        val voiceEnhancement = AudioEnhancement.createForVoiceContent()
        val musicEnhancement = AudioEnhancement.createForMusic()
        
        useCase.applyEnhancement(voiceEnhancement)
        
        val comparison1 = useCase.compareEnhancements(voiceEnhancement, musicEnhancement)
        assertTrue(comparison1.contains("better suited"), "Should provide meaningful comparison")
        
        val comparison2 = useCase.compareEnhancements(voiceEnhancement, voiceEnhancement)
        assertTrue(comparison2.contains("equally suited"), "Should detect equal enhancements")
        
        useCase.applyEnhancement(musicEnhancement)
        
        val comparison3 = useCase.compareEnhancements(voiceEnhancement, musicEnhancement)
        assertTrue(comparison3.contains("Second enhancement") || comparison3.contains("equally"), 
                   "Should favor music enhancement for music content")
    }
}