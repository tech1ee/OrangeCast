package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.*
import dev.orangecast.shared.domain.repository.AudioEnhancementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class AudioEnhancementUseCaseTest {
    
    private class TestAudioEnhancementRepository : AudioEnhancementRepository {
        private var currentEnhancement = AudioEnhancement.createDefault()
        private val userPresets = mutableMapOf<String, AudioEnhancement>()
        private val processingState = MutableStateFlow(AudioProcessingState())
        var isSupported = true
        var latency = 20L
        var lowLatencyMode = false
        
        override suspend fun applyEnhancement(enhancement: AudioEnhancement): Boolean {
            currentEnhancement = enhancement
            processingState.value = processingState.value.copy(
                currentEnhancement = enhancement,
                isProcessing = false
            )
            return true
        }
        
        override suspend fun getCurrentEnhancement(): AudioEnhancement {
            return currentEnhancement
        }
        
        override suspend fun resetEnhancement() {
            currentEnhancement = AudioEnhancement.createDefault()
        }
        
        override suspend fun saveUserPreset(name: String, enhancement: AudioEnhancement) {
            userPresets[name] = enhancement
        }
        
        override suspend fun getUserPresets(): Map<String, AudioEnhancement> {
            return userPresets.toMap()
        }
        
        override suspend fun deleteUserPreset(name: String) {
            userPresets.remove(name)
        }
        
        override fun observeProcessingState() = processingState
        
        override suspend fun analyzeCurrentAudio(): AudioAnalysisProfile? {
            return AudioAnalysisProfile(
                contentType = AudioContentType.VOICE_ONLY,
                averageVolumeDb = -20f,
                dynamicRange = 15f,
                voiceFrequencyRatio = 0.8f,
                musicFrequencyRatio = 0.2f,
                noiseFloor = -60f,
                recommendedEnhancement = AudioEnhancement.createForVoiceContent()
            )
        }
        
        override suspend fun isEnhancementSupported(): Boolean = isSupported
        override suspend fun getProcessingLatency(): Long = latency
        override suspend fun optimizeForLowLatency(enabled: Boolean) { lowLatencyMode = enabled }
    }
    
    private lateinit var repository: TestAudioEnhancementRepository
    private lateinit var useCase: AudioEnhancementUseCase
    
    @BeforeTest
    fun setup() {
        repository = TestAudioEnhancementRepository()
        useCase = AudioEnhancementUseCase(repository)
    }
    
    @Test
    fun applyEnhancement_shouldValidateAndApply() = runTest {
        val invalidEnhancement = AudioEnhancement(volumeBoost = 25f)
        
        val result = useCase.applyEnhancement(invalidEnhancement)
        
        assertTrue(result)
        val currentEnhancement = useCase.getCurrentEnhancement()
        assertEquals(20f, currentEnhancement.volumeBoost)
    }
    
    @Test
    fun applyPreset_withValidPreset_shouldReturnTrue() = runTest {
        val result = useCase.applyPreset("Voice Enhanced")
        
        assertTrue(result)
        val currentEnhancement = useCase.getCurrentEnhancement()
        assertEquals(AudioEnhancement.createForVoiceContent(), currentEnhancement)
    }
    
    @Test
    fun applyPreset_withInvalidPreset_shouldReturnFalse() = runTest {
        val result = useCase.applyPreset("Nonexistent Preset")
        
        assertFalse(result)
    }
    
    @Test
    fun resetToDefault_shouldApplyDefaultEnhancement() = runTest {
        useCase.applyEnhancement(AudioEnhancement.createForVoiceContent())
        
        val result = useCase.resetToDefault()
        
        assertTrue(result)
        val currentEnhancement = useCase.getCurrentEnhancement()
        assertEquals(AudioEnhancement.createDefault(), currentEnhancement)
    }
    
    @Test
    fun saveCustomPreset_shouldSaveValidatedEnhancement() = runTest {
        val enhancement = AudioEnhancement(volumeBoost = 25f)
        
        val result = useCase.saveCustomPreset("My Preset", enhancement)
        
        assertTrue(result)
        val presets = useCase.getAllPresets()
        val savedPreset = presets["My Preset"]
        assertNotNull(savedPreset)
        assertEquals(20f, savedPreset.volumeBoost)
    }
    
    @Test
    fun deleteCustomPreset_shouldRemovePreset() = runTest {
        useCase.saveCustomPreset("Temporary Preset", AudioEnhancement.createDefault())
        
        val result = useCase.deleteCustomPreset("Temporary Preset")
        
        assertTrue(result)
        val presets = useCase.getAllPresets()
        assertFalse(presets.containsKey("Temporary Preset"))
    }
    
    @Test
    fun getAllPresets_shouldIncludeBuiltInAndUserPresets() = runTest {
        useCase.saveCustomPreset("Custom Preset", AudioEnhancement.createDefault())
        
        val allPresets = useCase.getAllPresets()
        
        assertTrue(allPresets.size > 5)
        assertTrue(allPresets.containsKey("Default"))
        assertTrue(allPresets.containsKey("Voice Enhanced"))
        assertTrue(allPresets.containsKey("Custom Preset"))
    }
    
    @Test
    fun analyzeAndRecommend_shouldReturnRecommendation() = runTest {
        val recommendation = useCase.analyzeAndRecommend()
        
        assertNotNull(recommendation)
        assertEquals(AudioEnhancement.createForVoiceContent(), recommendation)
    }
    
    @Test
    fun getOptimalEnhancement_shouldReturnRecommendationOrDefault() = runTest {
        val optimal = useCase.getOptimalEnhancement()
        
        assertNotNull(optimal)
        assertEquals(AudioEnhancement.createForVoiceContent(), optimal)
    }
    
    @Test
    fun isEnhancementSupported_shouldReturnRepositoryResult() = runTest {
        repository.isSupported = false
        
        val result = useCase.isEnhancementSupported()
        
        assertFalse(result)
    }
    
    @Test
    fun observeProcessingState_shouldReturnRepositoryFlow() = runTest {
        val processingState = useCase.observeProcessingState().first()
        
        assertEquals(AudioEnhancement.createDefault(), processingState.currentEnhancement)
        assertFalse(processingState.isProcessing)
    }
    
    @Test
    fun createCustomEnhancement_shouldReturnValidatedEnhancement() = runTest {
        val enhancement = useCase.createCustomEnhancement(
            volumeBoost = 25f,
            voiceBoost = 150f,
            bassBoost = -50f,
            trebleBoost = 40f,
            noiseReduction = -10f
        )
        
        assertEquals(20f, enhancement.volumeBoost)
        assertEquals(100f, enhancement.voiceBoost)
        assertEquals(-30f, enhancement.bassBoost)
        assertEquals(30f, enhancement.trebleBoost)
        assertEquals(0f, enhancement.noiseReduction)
    }
    
    @Test
    fun adjustVolumeBoost_shouldConstrainValues() = runTest {
        val baseEnhancement = AudioEnhancement(volumeBoost = 15f)
        
        val increased = useCase.adjustVolumeBoost(baseEnhancement, 10f)
        val decreased = useCase.adjustVolumeBoost(baseEnhancement, -30f)
        
        assertEquals(20f, increased.volumeBoost)
        assertEquals(-15f, decreased.volumeBoost)
    }
    
    @Test
    fun adjustVoiceBoost_shouldConstrainValues() = runTest {
        val baseEnhancement = AudioEnhancement(voiceBoost = 50f)
        
        val increased = useCase.adjustVoiceBoost(baseEnhancement, 60f)
        val decreased = useCase.adjustVoiceBoost(baseEnhancement, -60f)
        
        assertEquals(100f, increased.voiceBoost)
        assertEquals(0f, decreased.voiceBoost)
    }
    
    @Test
    fun toggleNormalization_shouldFlipValue() = runTest {
        val baseEnhancement = AudioEnhancement(normalization = false)
        
        val toggled = useCase.toggleNormalization(baseEnhancement)
        val toggledAgain = useCase.toggleNormalization(toggled)
        
        assertTrue(toggled.normalization)
        assertFalse(toggledAgain.normalization)
    }
    
    @Test
    fun findBestPresetForCurrentAudio_shouldReturnHighestScoringPreset() = runTest {
        val result = useCase.findBestPresetForCurrentAudio()
        
        assertNotNull(result)
        assertEquals("Voice Enhanced", result.first)
    }
    
    @Test
    fun compareEnhancements_shouldProvideComparison() = runTest {
        val voiceEnhancement = AudioEnhancement.createForVoiceContent()
        val musicEnhancement = AudioEnhancement.createForMusic()
        
        val comparison = useCase.compareEnhancements(voiceEnhancement, musicEnhancement)
        
        assertTrue(comparison.contains("better suited") || comparison.contains("equally suited"))
    }
    
    @Test
    fun optimizeForPerformance_shouldCallRepository() = runTest {
        useCase.optimizeForPerformance(true)
        
        assertTrue(repository.lowLatencyMode)
    }
}