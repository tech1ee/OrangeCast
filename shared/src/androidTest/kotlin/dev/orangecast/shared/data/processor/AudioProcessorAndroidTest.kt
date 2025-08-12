package dev.orangecast.shared.data.processor

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.orangecast.shared.domain.model.AudioEnhancement
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.*

@RunWith(AndroidJUnit4::class)
class AudioProcessorAndroidTest {
    
    private lateinit var audioProcessor: AudioProcessor
    private lateinit var context: Context
    
    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        audioProcessor = AudioProcessor()
    }
    
    @Test
    fun isEnhancementSupported_shouldReturnTrueOnAndroid() = runTest {
        val isSupported = audioProcessor.isEnhancementSupported()
        
        assertTrue(isSupported, "Audio enhancement should be supported on Android")
    }
    
    @Test
    fun getProcessingLatency_shouldReturnReasonableValue() = runTest {
        val latency = audioProcessor.getProcessingLatency()
        
        assertTrue(latency > 0, "Processing latency should be positive")
        assertTrue(latency < 1000, "Processing latency should be reasonable (< 1s)")
    }
    
    @Test
    fun getCpuUsage_shouldReturnValidRange() = runTest {
        val cpuUsage = audioProcessor.getCpuUsage()
        
        assertTrue(cpuUsage >= 0f, "CPU usage should not be negative")
        assertTrue(cpuUsage <= 100f, "CPU usage should not exceed 100%")
    }
    
    @Test
    fun applyEnhancement_withValidAudioSessionId_shouldReturnTrue() = runTest {
        audioProcessor.setAudioSessionId(1234)
        val enhancement = AudioEnhancement.createForVoiceContent()
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Enhancement should be applied with valid audio session ID")
    }
    
    @Test
    fun applyEnhancement_withDefaultEnhancement_shouldReturnTrue() = runTest {
        audioProcessor.setAudioSessionId(1234)
        val enhancement = AudioEnhancement.createDefault()
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Default enhancement should be applied successfully")
    }
    
    @Test
    fun applyEnhancement_withVolumeBoost_shouldReturnTrue() = runTest {
        audioProcessor.setAudioSessionId(1234)
        val enhancement = AudioEnhancement(volumeBoost = 10f)
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Volume boost enhancement should be applied")
    }
    
    @Test
    fun applyEnhancement_withBassBoost_shouldReturnTrue() = runTest {
        audioProcessor.setAudioSessionId(1234)
        val enhancement = AudioEnhancement(bassBoost = 15f)
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Bass boost enhancement should be applied")
    }
    
    @Test
    fun applyEnhancement_withTrebleBoost_shouldReturnTrue() = runTest {
        audioProcessor.setAudioSessionId(1234)
        val enhancement = AudioEnhancement(trebleBoost = 12f)
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Treble boost enhancement should be applied")
    }
    
    @Test
    fun applyEnhancement_withVoiceBoost_shouldReturnTrue() = runTest {
        audioProcessor.setAudioSessionId(1234)
        val enhancement = AudioEnhancement(voiceBoost = 40f)
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Voice boost enhancement should be applied")
    }
    
    @Test
    fun applyEnhancement_withNoiseReduction_shouldReturnTrue() = runTest {
        audioProcessor.setAudioSessionId(1234)
        val enhancement = AudioEnhancement(noiseReduction = 25f)
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Noise reduction enhancement should be applied")
    }
    
    @Test
    fun applyEnhancement_withDynamicRangeCompression_shouldReturnTrue() = runTest {
        audioProcessor.setAudioSessionId(1234)
        val enhancement = AudioEnhancement(dynamicRangeCompression = true)
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Dynamic range compression should be applied")
    }
    
    @Test
    fun applyEnhancement_withComplexEnhancement_shouldReturnTrue() = runTest {
        audioProcessor.setAudioSessionId(1234)
        val enhancement = AudioEnhancement(
            volumeBoost = 8f,
            voiceBoost = 30f,
            bassBoost = 10f,
            trebleBoost = 5f,
            normalization = true,
            dynamicRangeCompression = true,
            noiseReduction = 20f
        )
        
        val result = audioProcessor.applyEnhancement(enhancement)
        
        assertTrue(result, "Complex enhancement should be applied successfully")
    }
    
    @Test
    fun optimizeForLowLatency_shouldAffectProcessingLatency() = runTest {
        val normalLatency = audioProcessor.getProcessingLatency()
        
        audioProcessor.optimizeForLowLatency(true)
        val lowLatency = audioProcessor.getProcessingLatency()
        
        assertTrue(lowLatency <= normalLatency, "Low latency optimization should reduce or maintain latency")
        assertTrue(lowLatency <= 20, "Low latency should be reasonably low")
        
        audioProcessor.optimizeForLowLatency(false)
        val restoredLatency = audioProcessor.getProcessingLatency()
        
        assertTrue(restoredLatency >= lowLatency, "Disabling low latency should restore normal latency")
    }
    
    @Test
    fun resetProcessor_shouldResetState() = runTest {
        audioProcessor.setAudioSessionId(1234)
        audioProcessor.applyEnhancement(AudioEnhancement.createForVoiceContent())
        
        audioProcessor.resetProcessor()
        
        val result = audioProcessor.applyEnhancement(AudioEnhancement.createDefault())
        assertTrue(result, "Processor should work after reset")
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
    fun setAudioSessionId_multipleCallsShouldWork() = runTest {
        audioProcessor.setAudioSessionId(1111)
        val result1 = audioProcessor.applyEnhancement(AudioEnhancement.createDefault())
        assertTrue(result1, "Should work with first session ID")
        
        audioProcessor.setAudioSessionId(2222)
        val result2 = audioProcessor.applyEnhancement(AudioEnhancement.createDefault())
        assertTrue(result2, "Should work with second session ID")
        
        audioProcessor.setAudioSessionId(3333)
        val result3 = audioProcessor.applyEnhancement(AudioEnhancement.createForVoiceContent())
        assertTrue(result3, "Should work with third session ID and different enhancement")
    }
    
    @Test
    fun getCpuUsage_withDifferentEnhancements_shouldVary() = runTest {
        audioProcessor.setAudioSessionId(1234)
        
        audioProcessor.applyEnhancement(AudioEnhancement.createDefault())
        val defaultCpuUsage = audioProcessor.getCpuUsage()
        
        audioProcessor.applyEnhancement(AudioEnhancement.createForVoiceContent())
        val enhancedCpuUsage = audioProcessor.getCpuUsage()
        
        assertTrue(enhancedCpuUsage >= defaultCpuUsage, "Enhanced processing should use same or more CPU")
    }
}