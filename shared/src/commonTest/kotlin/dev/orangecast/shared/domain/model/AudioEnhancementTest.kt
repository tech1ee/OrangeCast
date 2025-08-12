package dev.orangecast.shared.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class AudioEnhancementTest {
    
    @Test
    fun createDefault_shouldReturnZeroValues() {
        val enhancement = AudioEnhancement.createDefault()
        
        assertEquals(0f, enhancement.volumeBoost)
        assertEquals(0f, enhancement.voiceBoost)
        assertEquals(0f, enhancement.bassBoost)
        assertEquals(0f, enhancement.trebleBoost)
        assertEquals(0f, enhancement.noiseReduction)
        assertFalse(enhancement.normalization)
        assertFalse(enhancement.dynamicRangeCompression)
        assertFalse(enhancement.isEnhanced())
    }
    
    @Test
    fun createForVoiceContent_shouldHaveVoiceOptimizations() {
        val enhancement = AudioEnhancement.createForVoiceContent()
        
        assertEquals(3f, enhancement.volumeBoost)
        assertEquals(25f, enhancement.voiceBoost)
        assertEquals(0f, enhancement.bassBoost)
        assertEquals(10f, enhancement.trebleBoost)
        assertEquals(15f, enhancement.noiseReduction)
        assertTrue(enhancement.normalization)
        assertTrue(enhancement.dynamicRangeCompression)
        assertTrue(enhancement.isEnhanced())
    }
    
    @Test
    fun createForMusic_shouldHaveMusicOptimizations() {
        val enhancement = AudioEnhancement.createForMusic()
        
        assertEquals(0f, enhancement.volumeBoost)
        assertEquals(0f, enhancement.voiceBoost)
        assertEquals(15f, enhancement.bassBoost)
        assertEquals(5f, enhancement.trebleBoost)
        assertEquals(5f, enhancement.noiseReduction)
        assertTrue(enhancement.normalization)
        assertFalse(enhancement.dynamicRangeCompression)
        assertTrue(enhancement.isEnhanced())
    }
    
    @Test
    fun validate_shouldConstrainValues() {
        val invalidEnhancement = AudioEnhancement(
            volumeBoost = 25f,  // Over max of 20f
            voiceBoost = 150f,  // Over max of 100f
            bassBoost = -40f,   // Under min of -30f
            trebleBoost = 35f,  // Over max of 30f
            noiseReduction = -10f // Under min of 0f
        )
        
        val validated = invalidEnhancement.validate()
        
        assertEquals(20f, validated.volumeBoost)
        assertEquals(100f, validated.voiceBoost)
        assertEquals(-30f, validated.bassBoost)
        assertEquals(30f, validated.trebleBoost)
        assertEquals(0f, validated.noiseReduction)
    }
    
    @Test
    fun getVolumeMultiplier_shouldCalculateCorrectly() {
        val enhancement = AudioEnhancement(volumeBoost = 6f)
        val multiplier = enhancement.getVolumeMultiplier()
        
        val expected = kotlin.math.pow(10.0, (6.0 / 20.0)).toFloat()
        assertEquals(expected, multiplier, 0.001f)
    }
    
    @Test
    fun getVolumeMultiplier_shouldConstrainRange() {
        val extremePositive = AudioEnhancement(volumeBoost = 50f)
        val extremeNegative = AudioEnhancement(volumeBoost = -50f)
        
        val positiveMultiplier = extremePositive.getVolumeMultiplier()
        val negativeMultiplier = extremeNegative.getVolumeMultiplier()
        
        assertTrue(positiveMultiplier <= 10f)
        assertTrue(negativeMultiplier >= 0.1f)
    }
    
    @Test
    fun isEnhanced_shouldDetectNonDefaultValues() {
        val defaultEnhancement = AudioEnhancement.createDefault()
        assertFalse(defaultEnhancement.isEnhanced())
        
        val volumeBoosted = defaultEnhancement.copy(volumeBoost = 1f)
        assertTrue(volumeBoosted.isEnhanced())
        
        val voiceBoosted = defaultEnhancement.copy(voiceBoost = 10f)
        assertTrue(voiceBoosted.isEnhanced())
        
        val normalized = defaultEnhancement.copy(normalization = true)
        assertTrue(normalized.isEnhanced())
    }
    
    @Test
    fun getPresets_shouldReturnAllBuiltInPresets() {
        val presets = AudioEnhancement.getPresets()
        
        assertEquals(5, presets.size)
        
        val presetNames = presets.map { it.first }
        assertTrue(presetNames.contains("Default"))
        assertTrue(presetNames.contains("Voice Enhanced"))
        assertTrue(presetNames.contains("Music Optimized"))
        assertTrue(presetNames.contains("Night Mode"))
        assertTrue(presetNames.contains("Clarity Max"))
    }
    
    @Test
    fun nightModePreset_shouldHaveCorrectSettings() {
        val presets = AudioEnhancement.getPresets()
        val nightMode = presets.find { it.first == "Night Mode" }?.second
        
        assertNotNull(nightMode)
        assertEquals(5f, nightMode.volumeBoost)
        assertEquals(40f, nightMode.voiceBoost)
        assertEquals(-10f, nightMode.bassBoost)
        assertEquals(15f, nightMode.trebleBoost)
        assertEquals(25f, nightMode.noiseReduction)
        assertTrue(nightMode.normalization)
        assertTrue(nightMode.dynamicRangeCompression)
    }
    
    @Test
    fun clarityMaxPreset_shouldHaveMaxClarity() {
        val presets = AudioEnhancement.getPresets()
        val clarityMax = presets.find { it.first == "Clarity Max" }?.second
        
        assertNotNull(clarityMax)
        assertEquals(6f, clarityMax.volumeBoost)
        assertEquals(50f, clarityMax.voiceBoost)
        assertEquals(-15f, clarityMax.bassBoost)
        assertEquals(25f, clarityMax.trebleBoost)
        assertEquals(30f, clarityMax.noiseReduction)
        assertTrue(clarityMax.normalization)
        assertTrue(clarityMax.dynamicRangeCompression)
    }
}