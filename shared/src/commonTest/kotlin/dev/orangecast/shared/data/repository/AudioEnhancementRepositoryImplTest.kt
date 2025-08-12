package dev.orangecast.shared.data.repository

import dev.orangecast.shared.domain.model.*
import dev.orangecast.shared.data.processor.AudioProcessor
import dev.orangecast.shared.data.storage.PreferencesStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class AudioEnhancementRepositoryImplTest {
    
    private class TestAudioProcessor : AudioProcessor {
        var appliedEnhancement: AudioEnhancement? = null
        var isSupported = true
        var latency = 20L
        var cpuUsage = 10f
        var lowLatencyMode = false
        var analysisProfile: AudioAnalysisProfile? = null
        
        override suspend fun applyEnhancement(enhancement: AudioEnhancement): Boolean {
            appliedEnhancement = enhancement
            return true
        }
        
        override suspend fun analyzeCurrentAudio(): AudioAnalysisProfile? {
            return analysisProfile
        }
        
        override suspend fun isEnhancementSupported(): Boolean = isSupported
        override suspend fun getProcessingLatency(): Long = latency
        override suspend fun getCpuUsage(): Float = cpuUsage
        override suspend fun optimizeForLowLatency(enabled: Boolean) { lowLatencyMode = enabled }
        override suspend fun resetProcessor() { appliedEnhancement = null }
    }
    
    private class TestPreferencesStorage : PreferencesStorage {
        private var currentEnhancement: AudioEnhancement? = null
        private val userPresets = mutableMapOf<String, AudioEnhancement>()
        
        override suspend fun saveCurrentEnhancement(enhancement: AudioEnhancement) {
            currentEnhancement = enhancement
        }
        
        override suspend fun getCurrentEnhancement(): AudioEnhancement? {
            return currentEnhancement
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
        
        override suspend fun clearAllPresets() {
            userPresets.clear()
            currentEnhancement = null
        }
    }
    
    private lateinit var audioProcessor: TestAudioProcessor
    private lateinit var preferencesStorage: TestPreferencesStorage
    private lateinit var repository: AudioEnhancementRepositoryImpl
    
    @BeforeTest
    fun setup() {
        audioProcessor = TestAudioProcessor()
        preferencesStorage = TestPreferencesStorage()
        repository = AudioEnhancementRepositoryImpl(audioProcessor, preferencesStorage)
    }
    
    @Test
    fun applyEnhancement_shouldValidateAndProcessEnhancement() = runTest {
        val invalidEnhancement = AudioEnhancement(volumeBoost = 25f)
        
        val result = repository.applyEnhancement(invalidEnhancement)
        
        assertTrue(result)
        assertNotNull(audioProcessor.appliedEnhancement)
        assertEquals(20f, audioProcessor.appliedEnhancement?.volumeBoost)
    }
    
    @Test
    fun applyEnhancement_shouldUpdateProcessingState() = runTest {
        val enhancement = AudioEnhancement(volumeBoost = 5f)
        
        repository.applyEnhancement(enhancement)
        val processingState = repository.observeProcessingState().first()
        
        assertEquals(enhancement, processingState.currentEnhancement)
        assertFalse(processingState.isProcessing)
        assertEquals(20L, processingState.processingLatencyMs)
        assertEquals(10f, processingState.cpuUsagePercent)
    }
    
    @Test
    fun applyEnhancement_shouldSaveToPreferences() = runTest {
        val enhancement = AudioEnhancement(voiceBoost = 30f)
        
        repository.applyEnhancement(enhancement)
        
        assertEquals(enhancement, preferencesStorage.getCurrentEnhancement())
    }
    
    @Test
    fun getCurrentEnhancement_shouldReturnFromPreferencesIfNotCached() = runTest {
        val storedEnhancement = AudioEnhancement(bassBoost = 10f)
        preferencesStorage.saveCurrentEnhancement(storedEnhancement)
        
        val currentEnhancement = repository.getCurrentEnhancement()
        
        assertEquals(storedEnhancement, currentEnhancement)
    }
    
    @Test
    fun getCurrentEnhancement_shouldReturnDefaultIfNotStored() = runTest {
        val currentEnhancement = repository.getCurrentEnhancement()
        
        assertEquals(AudioEnhancement.createDefault(), currentEnhancement)
    }
    
    @Test
    fun resetEnhancement_shouldApplyDefaultEnhancement() = runTest {
        repository.applyEnhancement(AudioEnhancement.createForVoiceContent())
        
        repository.resetEnhancement()
        
        assertEquals(AudioEnhancement.createDefault(), audioProcessor.appliedEnhancement)
    }
    
    @Test
    fun saveUserPreset_shouldValidateAndStoreEnhancement() = runTest {
        val invalidEnhancement = AudioEnhancement(trebleBoost = 40f)
        
        repository.saveUserPreset("Test Preset", invalidEnhancement)
        
        val presets = repository.getUserPresets()
        val savedPreset = presets["Test Preset"]
        assertNotNull(savedPreset)
        assertEquals(30f, savedPreset.trebleBoost)
    }
    
    @Test
    fun getUserPresets_shouldReturnStoredPresets() = runTest {
        val preset1 = AudioEnhancement(volumeBoost = 5f)
        val preset2 = AudioEnhancement(voiceBoost = 20f)
        
        repository.saveUserPreset("Preset 1", preset1)
        repository.saveUserPreset("Preset 2", preset2)
        
        val presets = repository.getUserPresets()
        
        assertEquals(2, presets.size)
        assertEquals(preset1, presets["Preset 1"])
        assertEquals(preset2, presets["Preset 2"])
    }
    
    @Test
    fun deleteUserPreset_shouldRemovePreset() = runTest {
        repository.saveUserPreset("Temporary", AudioEnhancement.createDefault())
        
        repository.deleteUserPreset("Temporary")
        
        val presets = repository.getUserPresets()
        assertFalse(presets.containsKey("Temporary"))
    }
    
    @Test
    fun observeProcessingState_shouldReturnStateFlow() = runTest {
        val initialState = repository.observeProcessingState().first()
        
        assertEquals(AudioEnhancement.createDefault(), initialState.currentEnhancement)
        assertFalse(initialState.isProcessing)
    }
    
    @Test
    fun analyzeCurrentAudio_shouldReturnProcessorAnalysis() = runTest {
        val mockProfile = AudioAnalysisProfile(
            contentType = AudioContentType.MUSIC_ONLY,
            averageVolumeDb = -15f,
            dynamicRange = 40f,
            voiceFrequencyRatio = 0.1f,
            musicFrequencyRatio = 0.9f,
            noiseFloor = -70f,
            recommendedEnhancement = AudioEnhancement.createForMusic()
        )
        audioProcessor.analysisProfile = mockProfile
        
        val result = repository.analyzeCurrentAudio()
        
        assertEquals(mockProfile, result)
    }
    
    @Test
    fun isEnhancementSupported_shouldReturnProcessorCapability() = runTest {
        audioProcessor.isSupported = false
        
        val result = repository.isEnhancementSupported()
        
        assertFalse(result)
    }
    
    @Test
    fun getProcessingLatency_shouldReturnProcessorLatency() = runTest {
        audioProcessor.latency = 50L
        
        val result = repository.getProcessingLatency()
        
        assertEquals(50L, result)
    }
    
    @Test
    fun optimizeForLowLatency_shouldConfigureProcessor() = runTest {
        repository.optimizeForLowLatency(true)
        
        assertTrue(audioProcessor.lowLatencyMode)
    }
    
    @Test
    fun applyEnhancement_withProcessorFailure_shouldReturnFalse() = runTest {
        val testProcessor = object : TestAudioProcessor() {
            override suspend fun applyEnhancement(enhancement: AudioEnhancement): Boolean {
                return false
            }
        }
        val failureRepository = AudioEnhancementRepositoryImpl(testProcessor, preferencesStorage)
        
        val result = failureRepository.applyEnhancement(AudioEnhancement.createDefault())
        
        assertFalse(result)
    }
    
    @Test
    fun applyEnhancement_shouldSetProcessingStateDuringOperation() = runTest {
        var processingStateWhenCalled = false
        val slowProcessor = object : TestAudioProcessor() {
            override suspend fun applyEnhancement(enhancement: AudioEnhancement): Boolean {
                val state = repository.observeProcessingState().first()
                processingStateWhenCalled = state.isProcessing
                return super.applyEnhancement(enhancement)
            }
        }
        val processingRepository = AudioEnhancementRepositoryImpl(slowProcessor, preferencesStorage)
        
        processingRepository.applyEnhancement(AudioEnhancement.createDefault())
        
        assertTrue(processingStateWhenCalled)
    }
}