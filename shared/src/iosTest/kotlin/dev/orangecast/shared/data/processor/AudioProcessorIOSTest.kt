package dev.orangecast.shared.data.processor

import dev.orangecast.shared.domain.model.AudioEnhancement
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class AudioProcessorIOSTest {
    
    private lateinit var audioProcessor: AudioProcessor
    
    @BeforeTest
    fun setup() {
        audioProcessor = AudioProcessor()
    }
    
    @AfterTest
    fun tearDown() = runTest {
        audioProcessor.resetProcessor()
    }
    
    @Test
    fun isEnhancementSupported_shouldReturnTrueOnIOS() = runTest {
        val isSupported = audioProcessor.isEnhancementSupported()
        
        assertTrue(isSupported, "Audio enhancement should be supported on iOS")
    }
    
    @Test
    fun getProcessingLatency_shouldReturnLowLatency() = runTest {
        val latency = audioProcessor.getProcessingLatency()
        
        assertTrue(latency > 0, "Processing latency should be positive")
        assertTrue(latency < 100, "iOS should have low processing latency")
    }
    
    @Test
    fun getCpuUsage_shouldReturnValidRange() = runTest {
        val cpuUsage = audioProcessor.getCpuUsage()
        
        assertTrue(cpuUsage >= 0f, "CPU usage should not be negative")
        assertTrue(cpuUsage <= 100f, "CPU usage should not exceed 100%")
    }
    
    @Test
    fun applyEnhancement_withDefaultEnhancement_shouldReturnTrue() = runTest {
        val enhancement = AudioEnhancement.createDefault()
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Default enhancement should be applied successfully")
    }
    
    @Test
    fun applyEnhancement_withVoiceEnhancement_shouldReturnTrue() = runTest {
        val enhancement = AudioEnhancement.createForVoiceContent()
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Voice enhancement should be applied successfully")
    }
    
    @Test
    fun applyEnhancement_withMusicEnhancement_shouldReturnTrue() = runTest {
        val enhancement = AudioEnhancement.createForMusic()
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Music enhancement should be applied successfully")
    }
    
    @Test
    fun applyEnhancement_withVolumeBoost_shouldReturnTrue() = runTest {
        val enhancement = AudioEnhancement(volumeBoost = 12f)
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Volume boost enhancement should be applied")
    }
    
    @Test
    fun applyEnhancement_withVoiceBoost_shouldReturnTrue() = runTest {
        val enhancement = AudioEnhancement(voiceBoost = 35f)
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Voice boost enhancement should be applied")
    }
    
    @Test
    fun applyEnhancement_withBassBoost_shouldReturnTrue() = runTest {
        val enhancement = AudioEnhancement(bassBoost = 18f)
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Bass boost enhancement should be applied")
    }
    
    @Test
    fun applyEnhancement_withTrebleBoost_shouldReturnTrue() = runTest {
        val enhancement = AudioEnhancement(trebleBoost = 15f)
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Treble boost enhancement should be applied")
    }
    
    @Test
    fun applyEnhancement_withNormalization_shouldReturnTrue() = runTest {
        val enhancement = AudioEnhancement(normalization = true)
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Normalization should be applied")
    }
    
    @Test
    fun applyEnhancement_withDynamicRangeCompression_shouldReturnTrue() = runTest {
        val enhancement = AudioEnhancement(dynamicRangeCompression = true)
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Dynamic range compression should be applied")
    }
    
    @Test
    fun applyEnhancement_withComplexEnhancement_shouldReturnTrue() = runTest {
        val enhancement = AudioEnhancement(
            volumeBoost = 6f,
            voiceBoost = 45f,
            bassBoost = 12f,
            trebleBoost = 8f,
            normalization = true,
            dynamicRangeCompression = true,
            noiseReduction = 30f
        )
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Complex enhancement should be applied successfully")
    }
    
    @Test
    fun optimizeForLowLatency_shouldReduceLatency() = runTest {
        val normalLatency = audioProcessor.getProcessingLatency()
        
        audioProcessor.optimizeForLowLatency(true)
        val lowLatency = audioProcessor.getProcessingLatency()
        
        assertTrue(lowLatency <= normalLatency, "Low latency optimization should reduce or maintain latency")
        assertTrue(lowLatency <= 10, "iOS low latency should be very low")
        
        audioProcessor.optimizeForLowLatency(false)
        val restoredLatency = audioProcessor.getProcessingLatency()
        
        assertTrue(restoredLatency >= lowLatency, "Disabling low latency should restore normal latency")
    }
    
    @Test
    fun analyzeCurrentAudio_shouldReturnValidAnalysis() = runTest {
        val analysis = audioProcessor.analyzeCurrentAudio()
        
        if (analysis != null) {
            assertTrue(analysis.averageVolumeDb < 0, "Average volume should be negative (dB scale)")
            assertTrue(analysis.dynamicRange >= 0, "Dynamic range should be non-negative")
            assertTrue(analysis.voiceFrequencyRatio in 0f..1f, "Voice frequency ratio should be in valid range")
            assertTrue(analysis.musicFrequencyRatio in 0f..1f, "Music frequency ratio should be in valid range")
            assertTrue(analysis.noiseFloor < 0, "Noise floor should be negative (dB scale)")
            assertNotNull(analysis.recommendedEnhancement, "Should provide recommended enhancement")
        }
    }
    
    @Test
    fun resetProcessor_shouldResetState() = runTest {
        audioProcessor.applyEnhancement(AudioEnhancement.createForVoiceContent())
        
        audioProcessor.resetProcessor()
        
        val result = audioProcessor.applyEnhancement(AudioEnhancement.createDefault())
        assertTrue(result, "Processor should work after reset")
    }
    
    @Test
    fun sequentialEnhancements_shouldAllWork() = runTest {
        val enhancements = listOf(
            AudioEnhancement.createDefault(),
            AudioEnhancement.createForVoiceContent(),
            AudioEnhancement.createForMusic(),
            AudioEnhancement(volumeBoost = 10f, bassBoost = 20f),
            AudioEnhancement(voiceBoost = 50f, trebleBoost = 15f, normalization = true)
        )
        
        enhancements.forEach { enhancement ->
            val result = audioProcessor.applyEnhancement(enhancement)
            assertTrue(result, "Sequential enhancement should be applied: $enhancement")
        }
    }
    
    @Test
    fun getCpuUsage_withDifferentEnhancements_shouldVary() = runTest {
        audioProcessor.applyEnhancement(AudioEnhancement.createDefault())
        val defaultCpuUsage = audioProcessor.getCpuUsage()
        
        audioProcessor.applyEnhancement(AudioEnhancement(
            volumeBoost = 15f,
            voiceBoost = 50f,
            bassBoost = 20f,
            trebleBoost = 15f,
            normalization = true,
            dynamicRangeCompression = true
        ))
        val enhancedCpuUsage = audioProcessor.getCpuUsage()
        
        assertTrue(enhancedCpuUsage >= defaultCpuUsage, "Enhanced processing should use same or more CPU")
    }
    
    @Test
    fun extremeValues_shouldBeHandledGracefully() = runTest {
        val extremeEnhancement = AudioEnhancement(
            volumeBoost = 20f,
            voiceBoost = 100f,
            bassBoost = 30f,
            trebleBoost = 30f,
            normalization = true,
            dynamicRangeCompression = true,
            noiseReduction = 100f
        )
        
        val result = audioProcessor.applyEnhancement(extremeEnhancement)
        
        assertTrue(result, "Extreme values should be handled gracefully")
        
        val latency = audioProcessor.getProcessingLatency()
        assertTrue(latency > 0, "Processing should still work with extreme values")
        
        val cpuUsage = audioProcessor.getCpuUsage()
        assertTrue(cpuUsage <= 100f, "CPU usage should remain within bounds")
    }
}