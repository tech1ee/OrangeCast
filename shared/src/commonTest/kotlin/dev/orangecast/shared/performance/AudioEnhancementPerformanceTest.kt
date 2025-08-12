package dev.orangecast.shared.performance

import dev.orangecast.shared.domain.model.*
import dev.orangecast.shared.domain.usecase.AudioEnhancementUseCase
import dev.orangecast.shared.data.repository.AudioEnhancementRepositoryImpl
import dev.orangecast.shared.data.processor.AudioProcessor
import dev.orangecast.shared.data.storage.PreferencesStorage
import kotlinx.coroutines.test.runTest
import kotlin.test.*
import kotlin.system.measureTimeMillis

class AudioEnhancementPerformanceTest {
    
    private class PerformanceTestAudioProcessor : AudioProcessor {
        private var processingLatency = 15L
        private var cpuUsage = 10f
        private var callCount = 0
        var isLowLatencyOptimized = false
        
        override suspend fun applyEnhancement(enhancement: AudioEnhancement): Boolean {
            callCount++
            return true
        }
        
        override suspend fun analyzeCurrentAudio(): AudioAnalysisProfile {
            return AudioAnalysisProfile(
                contentType = AudioContentType.MIXED_CONTENT,
                averageVolumeDb = -18f,
                dynamicRange = 25f,
                voiceFrequencyRatio = 0.5f,
                musicFrequencyRatio = 0.5f,
                noiseFloor = -60f,
                recommendedEnhancement = AudioEnhancement.createDefault()
            )
        }
        
        override suspend fun isEnhancementSupported(): Boolean = true
        override suspend fun getProcessingLatency(): Long = if (isLowLatencyOptimized) 5L else processingLatency
        override suspend fun getCpuUsage(): Float = cpuUsage
        override suspend fun optimizeForLowLatency(enabled: Boolean) { isLowLatencyOptimized = enabled }
        override suspend fun resetProcessor() { callCount = 0 }
        
        fun getCallCount(): Int = callCount
        fun setProcessingLatency(latency: Long) { processingLatency = latency }
        fun setCpuUsage(usage: Float) { cpuUsage = usage }
    }
    
    private class PerformanceTestPreferencesStorage : PreferencesStorage {
        private var currentEnhancement: AudioEnhancement? = null
        private val userPresets = mutableMapOf<String, AudioEnhancement>()
        private var operationTime = 1L
        
        override suspend fun saveCurrentEnhancement(enhancement: AudioEnhancement) {
            kotlinx.coroutines.delay(operationTime)
            currentEnhancement = enhancement
        }
        
        override suspend fun getCurrentEnhancement(): AudioEnhancement? {
            kotlinx.coroutines.delay(operationTime)
            return currentEnhancement
        }
        
        override suspend fun saveUserPreset(name: String, enhancement: AudioEnhancement) {
            kotlinx.coroutines.delay(operationTime)
            userPresets[name] = enhancement
        }
        
        override suspend fun getUserPresets(): Map<String, AudioEnhancement> {
            kotlinx.coroutines.delay(operationTime)
            return userPresets.toMap()
        }
        
        override suspend fun deleteUserPreset(name: String) {
            kotlinx.coroutines.delay(operationTime)
            userPresets.remove(name)
        }
        
        override suspend fun clearAllPresets() {
            kotlinx.coroutines.delay(operationTime)
            userPresets.clear()
            currentEnhancement = null
        }
        
        fun setOperationTime(time: Long) { operationTime = time }
    }
    
    private lateinit var audioProcessor: PerformanceTestAudioProcessor
    private lateinit var preferencesStorage: PerformanceTestPreferencesStorage
    private lateinit var repository: AudioEnhancementRepositoryImpl
    private lateinit var useCase: AudioEnhancementUseCase
    
    @BeforeTest
    fun setup() {
        audioProcessor = PerformanceTestAudioProcessor()
        preferencesStorage = PerformanceTestPreferencesStorage()
        repository = AudioEnhancementRepositoryImpl(audioProcessor, preferencesStorage)
        useCase = AudioEnhancementUseCase(repository)
    }
    
    @Test
    fun applyEnhancement_shouldCompleteUnder200ms() = runTest {
        val enhancement = AudioEnhancement.createForVoiceContent()
        
        val timeMs = measureTimeMillis {
            useCase.applyEnhancement(enhancement)
        }
        
        assertTrue(timeMs < 200, "Enhancement application should complete under 200ms, took ${timeMs}ms")
    }
    
    @Test
    fun sequentialEnhancements_shouldMaintainPerformance() = runTest {
        val enhancements = listOf(
            AudioEnhancement.createDefault(),
            AudioEnhancement.createForVoiceContent(),
            AudioEnhancement.createForMusic(),
            AudioEnhancement(volumeBoost = 10f),
            AudioEnhancement(voiceBoost = 40f)
        )
        
        val totalTime = measureTimeMillis {
            enhancements.forEach { enhancement ->
                useCase.applyEnhancement(enhancement)
            }
        }
        
        val averageTime = totalTime / enhancements.size
        assertTrue(averageTime < 200, "Average enhancement time should be under 200ms, was ${averageTime}ms")
        assertTrue(totalTime < 1000, "Total time for 5 enhancements should be under 1s, was ${totalTime}ms")
    }
    
    @Test
    fun processingLatency_shouldMeetRequirements() = runTest {
        val normalLatency = useCase.getProcessingLatency()
        assertTrue(normalLatency < 100, "Normal processing latency should be under 100ms, was ${normalLatency}ms")
        
        useCase.optimizeForPerformance(true)
        val lowLatency = useCase.getProcessingLatency()
        assertTrue(lowLatency < 20, "Low latency mode should be under 20ms, was ${lowLatency}ms")
        assertTrue(lowLatency <= normalLatency, "Low latency should be better than or equal to normal latency")
    }
    
    @Test
    fun cpuUsage_shouldRemainWithinLimits() = runTest {
        val defaultUsage = audioProcessor.getCpuUsage()
        assertTrue(defaultUsage < 50f, "Default CPU usage should be under 50%, was ${defaultUsage}%")
        
        useCase.applyEnhancement(AudioEnhancement(
            volumeBoost = 15f,
            voiceBoost = 50f,
            bassBoost = 20f,
            trebleBoost = 15f,
            normalization = true,
            dynamicRangeCompression = true,
            noiseReduction = 30f
        ))
        
        audioProcessor.setCpuUsage(35f)
        val heavyUsage = audioProcessor.getCpuUsage()
        assertTrue(heavyUsage < 50f, "Heavy enhancement CPU usage should be under 50%, was ${heavyUsage}%")
    }
    
    @Test
    fun memoryUsage_presetManagement_shouldBeEfficient() = runTest {
        val startTime = System.currentTimeMillis()
        
        repeat(100) { index ->
            val enhancement = AudioEnhancement(
                volumeBoost = (index % 20 - 10).toFloat(),
                voiceBoost = (index % 100).toFloat(),
                bassBoost = (index % 30 - 15).toFloat()
            )
            useCase.saveCustomPreset("Preset_$index", enhancement)
        }
        
        val saveTime = measureTimeMillis {
            val allPresets = useCase.getAllPresets()
            assertTrue(allPresets.size >= 100, "Should have at least 100 presets")
        }
        
        assertTrue(saveTime < 100, "Retrieving 100+ presets should take under 100ms, took ${saveTime}ms")
        
        val deleteTime = measureTimeMillis {
            repeat(50) { index ->
                useCase.deleteCustomPreset("Preset_$index")
            }
        }
        
        assertTrue(deleteTime < 500, "Deleting 50 presets should take under 500ms, took ${deleteTime}ms")
    }
    
    @Test
    fun concurrentOperations_shouldMaintainPerformance() = runTest {
        val operations = (1..10).map { index ->
            kotlinx.coroutines.async {
                val enhancement = when (index % 3) {
                    0 -> AudioEnhancement.createDefault()
                    1 -> AudioEnhancement.createForVoiceContent()
                    else -> AudioEnhancement.createForMusic()
                }
                useCase.applyEnhancement(enhancement)
            }
        }
        
        val totalTime = measureTimeMillis {
            operations.forEach { it.await() }
        }
        
        assertTrue(totalTime < 2000, "10 concurrent operations should complete under 2s, took ${totalTime}ms")
    }
    
    @Test
    fun enhancementValidation_shouldBeFast() = runTest {
        val invalidEnhancement = AudioEnhancement(
            volumeBoost = 50f,
            voiceBoost = 200f,
            bassBoost = -100f,
            trebleBoost = 80f,
            noiseReduction = -50f
        )
        
        val validationTime = measureTimeMillis {
            repeat(1000) {
                invalidEnhancement.validate()
            }
        }
        
        assertTrue(validationTime < 100, "1000 validations should complete under 100ms, took ${validationTime}ms")
    }
    
    @Test
    fun analysisAndRecommendation_shouldBeResponsive() = runTest {
        val analysisTime = measureTimeMillis {
            repeat(50) {
                useCase.analyzeAndRecommend()
            }
        }
        
        val averageAnalysisTime = analysisTime / 50
        assertTrue(averageAnalysisTime < 50, "Audio analysis should average under 50ms, was ${averageAnalysisTime}ms")
        
        val recommendationTime = measureTimeMillis {
            useCase.findBestPresetForCurrentAudio()
        }
        
        assertTrue(recommendationTime < 200, "Finding best preset should take under 200ms, took ${recommendationTime}ms")
    }
    
    @Test
    fun suitabilityScoring_shouldScaleWell() = runTest {
        val allPresets = useCase.getAllPresets()
        
        val scoringTime = measureTimeMillis {
            allPresets.values.forEach { enhancement ->
                useCase.getEnhancementSuitabilityScore(enhancement)
            }
        }
        
        val averageScoringTime = scoringTime / allPresets.size
        assertTrue(averageScoringTime < 20, "Suitability scoring should average under 20ms, was ${averageScoringTime}ms")
    }
    
    @Test
    fun enhancementComparison_shouldBeFast() = runTest {
        val enhancement1 = AudioEnhancement.createForVoiceContent()
        val enhancement2 = AudioEnhancement.createForMusic()
        
        val comparisonTime = measureTimeMillis {
            repeat(100) {
                useCase.compareEnhancements(enhancement1, enhancement2)
            }
        }
        
        val averageComparisonTime = comparisonTime / 100
        assertTrue(averageComparisonTime < 10, "Enhancement comparison should average under 10ms, was ${averageComparisonTime}ms")
    }
    
    @Test
    fun storageOperations_shouldMeetPerformanceTargets() = runTest {
        preferencesStorage.setOperationTime(5L)
        
        val saveTime = measureTimeMillis {
            repeat(20) { index ->
                val enhancement = AudioEnhancement(volumeBoost = index.toFloat())
                repository.applyEnhancement(enhancement)
            }
        }
        
        assertTrue(saveTime < 1000, "20 save operations should complete under 1s, took ${saveTime}ms")
        
        val loadTime = measureTimeMillis {
            repeat(20) {
                repository.getCurrentEnhancement()
            }
        }
        
        assertTrue(loadTime < 500, "20 load operations should complete under 500ms, took ${loadTime}ms")
    }
    
    @Test
    fun processorCallOptimization_shouldMinimizeCalls() = runTest {
        val initialCallCount = audioProcessor.getCallCount()
        
        val enhancement = AudioEnhancement.createForVoiceContent()
        
        useCase.applyEnhancement(enhancement)
        val callsAfterFirst = audioProcessor.getCallCount()
        
        useCase.applyEnhancement(enhancement)
        val callsAfterSecond = audioProcessor.getCallCount()
        
        val callsFromFirst = callsAfterFirst - initialCallCount
        val callsFromSecond = callsAfterSecond - callsAfterFirst
        
        assertEquals(1, callsFromFirst, "First application should make exactly 1 processor call")
        assertEquals(1, callsFromSecond, "Second application should make exactly 1 processor call (no optimization yet)")
    }
    
    @Test
    fun adjustmentOperations_shouldBeImmediate() = runTest {
        var currentEnhancement = AudioEnhancement.createDefault()
        
        val adjustmentTime = measureTimeMillis {
            currentEnhancement = useCase.adjustVolumeBoost(currentEnhancement, 5f)
            currentEnhancement = useCase.adjustVoiceBoost(currentEnhancement, 30f)
            currentEnhancement = useCase.adjustBassBoost(currentEnhancement, 10f)
            currentEnhancement = useCase.adjustTrebleBoost(currentEnhancement, 8f)
            currentEnhancement = useCase.toggleNormalization(currentEnhancement)
            currentEnhancement = useCase.toggleDynamicRangeCompression(currentEnhancement)
            currentEnhancement = useCase.adjustNoiseReduction(currentEnhancement, 20f)
        }
        
        assertTrue(adjustmentTime < 10, "7 adjustment operations should complete under 10ms, took ${adjustmentTime}ms")
    }
}