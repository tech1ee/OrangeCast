package dev.orangecast.shared.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AudioAnalysisProfileTest {
    
    @Test
    fun getSuitabilityScore_voiceOnlyContent_shouldFavorVoiceSettings() {
        val voiceProfile = AudioAnalysisProfile(
            contentType = AudioContentType.VOICE_ONLY,
            averageVolumeDb = -20f,
            dynamicRange = 15f,
            voiceFrequencyRatio = 0.8f,
            musicFrequencyRatio = 0.2f,
            noiseFloor = -60f,
            recommendedEnhancement = AudioEnhancement.createForVoiceContent()
        )
        
        val voiceEnhancement = AudioEnhancement(
            voiceBoost = 30f,
            bassBoost = -10f,
            noiseReduction = 20f
        )
        
        val musicEnhancement = AudioEnhancement(
            bassBoost = 20f,
            trebleBoost = 10f
        )
        
        val voiceScore = voiceProfile.getSuitabilityScore(voiceEnhancement)
        val musicScore = voiceProfile.getSuitabilityScore(musicEnhancement)
        
        assertTrue(voiceScore > musicScore, "Voice enhancement should score higher for voice content")
        assertTrue(voiceScore > 1.0f, "Voice enhancement should get bonus points")
    }
    
    @Test
    fun getSuitabilityScore_musicOnlyContent_shouldFavorMusicSettings() {
        val musicProfile = AudioAnalysisProfile(
            contentType = AudioContentType.MUSIC_ONLY,
            averageVolumeDb = -15f,
            dynamicRange = 40f,
            voiceFrequencyRatio = 0.1f,
            musicFrequencyRatio = 0.9f,
            noiseFloor = -70f,
            recommendedEnhancement = AudioEnhancement.createForMusic()
        )
        
        val musicEnhancement = AudioEnhancement(
            bassBoost = 15f,
            trebleBoost = 5f,
            dynamicRangeCompression = false
        )
        
        val voiceEnhancement = AudioEnhancement(
            voiceBoost = 40f,
            dynamicRangeCompression = true
        )
        
        val musicScore = musicProfile.getSuitabilityScore(musicEnhancement)
        val voiceScore = musicProfile.getSuitabilityScore(voiceEnhancement)
        
        assertTrue(musicScore > voiceScore, "Music enhancement should score higher for music content")
        assertTrue(musicScore > 1.0f, "Music enhancement should get bonus points")
    }
    
    @Test
    fun getSuitabilityScore_mixedContent_shouldFavorBalancedSettings() {
        val mixedProfile = AudioAnalysisProfile(
            contentType = AudioContentType.MIXED_CONTENT,
            averageVolumeDb = -18f,
            dynamicRange = 25f,
            voiceFrequencyRatio = 0.5f,
            musicFrequencyRatio = 0.5f,
            noiseFloor = -65f,
            recommendedEnhancement = AudioEnhancement.createDefault()
        )
        
        val balancedEnhancement = AudioEnhancement(
            voiceBoost = 20f,
            normalization = true
        )
        
        val extremeEnhancement = AudioEnhancement(
            voiceBoost = 90f,
            bassBoost = 25f
        )
        
        val balancedScore = mixedProfile.getSuitabilityScore(balancedEnhancement)
        val extremeScore = mixedProfile.getSuitabilityScore(extremeEnhancement)
        
        assertTrue(balancedScore > extremeScore, "Balanced settings should score higher for mixed content")
    }
    
    @Test
    fun getSuitabilityScore_shouldPenalizeExtremeSettings() {
        val profile = AudioAnalysisProfile(
            contentType = AudioContentType.VOICE_ONLY,
            averageVolumeDb = -20f,
            dynamicRange = 15f,
            voiceFrequencyRatio = 0.8f,
            musicFrequencyRatio = 0.2f,
            noiseFloor = -60f,
            recommendedEnhancement = AudioEnhancement.createForVoiceContent()
        )
        
        val moderateEnhancement = AudioEnhancement(volumeBoost = 10f, voiceBoost = 50f)
        val extremeEnhancement = AudioEnhancement(volumeBoost = 18f, voiceBoost = 90f)
        
        val moderateScore = profile.getSuitabilityScore(moderateEnhancement)
        val extremeScore = profile.getSuitabilityScore(extremeEnhancement)
        
        assertTrue(moderateScore > extremeScore, "Extreme settings should be penalized")
        assertTrue(extremeScore < 1.0f, "Extreme settings should score below baseline")
    }
    
    @Test
    fun getSuitabilityScore_shouldConstrainRange() {
        val profile = AudioAnalysisProfile(
            contentType = AudioContentType.UNKNOWN,
            averageVolumeDb = -20f,
            dynamicRange = 20f,
            voiceFrequencyRatio = 0.5f,
            musicFrequencyRatio = 0.5f,
            noiseFloor = -60f,
            recommendedEnhancement = AudioEnhancement.createDefault()
        )
        
        val maxEnhancement = AudioEnhancement(
            volumeBoost = 20f,
            voiceBoost = 100f,
            bassBoost = 30f,
            trebleBoost = 30f,
            normalization = true
        )
        
        val minEnhancement = AudioEnhancement(
            volumeBoost = -20f,
            voiceBoost = 0f,
            bassBoost = -30f,
            trebleBoost = -30f
        )
        
        val maxScore = profile.getSuitabilityScore(maxEnhancement)
        val minScore = profile.getSuitabilityScore(minEnhancement)
        
        assertTrue(maxScore in 0f..2f, "Score should be constrained to 0-2 range")
        assertTrue(minScore in 0f..2f, "Score should be constrained to 0-2 range")
    }
    
    @Test
    fun getSuitabilityScore_unknownContent_shouldUseNeutralScoring() {
        val unknownProfile = AudioAnalysisProfile(
            contentType = AudioContentType.UNKNOWN,
            averageVolumeDb = -20f,
            dynamicRange = 20f,
            voiceFrequencyRatio = 0.5f,
            musicFrequencyRatio = 0.5f,
            noiseFloor = -60f,
            recommendedEnhancement = AudioEnhancement.createDefault()
        )
        
        val voiceEnhancement = AudioEnhancement.createForVoiceContent()
        val musicEnhancement = AudioEnhancement.createForMusic()
        val defaultEnhancement = AudioEnhancement.createDefault()
        
        val voiceScore = unknownProfile.getSuitabilityScore(voiceEnhancement)
        val musicScore = unknownProfile.getSuitabilityScore(musicEnhancement)
        val defaultScore = unknownProfile.getSuitabilityScore(defaultEnhancement)
        
        assertEquals(1.0f, defaultScore, "Default should score baseline for unknown content")
        assertTrue(voiceScore >= 0.7f, "Voice enhancement should not be heavily penalized")
        assertTrue(musicScore >= 0.7f, "Music enhancement should not be heavily penalized")
    }
}