package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.AudioAnalysisResult
import dev.orangecast.shared.domain.model.SilenceAggressiveness
import dev.orangecast.shared.domain.model.SilenceDetectionConfig
import dev.orangecast.shared.domain.repository.PlayerStateRepository
import dev.orangecast.shared.domain.repository.SilenceDetectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class SkipSilenceUseCase(
    private val silenceDetectionRepository: SilenceDetectionRepository,
    private val playerStateRepository: PlayerStateRepository
) {

    suspend fun enableSkipSilenceForCurrentTrack() {
        val currentState = playerStateRepository.getCurrentState()
        val trackId = currentState.currentTrack?.id
        
        if (trackId != null) {
            silenceDetectionRepository.saveSkipSilencePreference(trackId, true)
        } else {
            silenceDetectionRepository.saveGlobalSkipSilencePreference(true)
        }
    }

    suspend fun disableSkipSilenceForCurrentTrack() {
        val currentState = playerStateRepository.getCurrentState()
        val trackId = currentState.currentTrack?.id
        
        if (trackId != null) {
            silenceDetectionRepository.saveSkipSilencePreference(trackId, false)
        } else {
            silenceDetectionRepository.saveGlobalSkipSilencePreference(false)
        }
    }

    suspend fun setAggressivenessForCurrentTrack(aggressiveness: SilenceAggressiveness) {
        val currentState = playerStateRepository.getCurrentState()
        val trackId = currentState.currentTrack?.id
        
        if (trackId != null) {
            silenceDetectionRepository.saveAggressivenessPreference(trackId, aggressiveness)
        } else {
            silenceDetectionRepository.saveGlobalAggressivenessPreference(aggressiveness)
        }
    }

    suspend fun isSkipSilenceEnabledForTrack(trackId: String): Boolean {
        return silenceDetectionRepository.getSkipSilencePreference(trackId)
    }

    suspend fun getAggressivenessForTrack(trackId: String): SilenceAggressiveness {
        return silenceDetectionRepository.getAggressivenessPreference(trackId)
    }

    suspend fun getConfigForCurrentTrack(): SilenceDetectionConfig? {
        val currentState = playerStateRepository.getCurrentState()
        val trackId = currentState.currentTrack?.id ?: return null
        
        val isEnabled = silenceDetectionRepository.getSkipSilencePreference(trackId)
        if (!isEnabled) return null
        
        val aggressiveness = silenceDetectionRepository.getAggressivenessPreference(trackId)
        return SilenceDetectionConfig.createConfig(aggressiveness)
    }

    suspend fun toggleSkipSilenceForCurrentTrack() {
        val currentState = playerStateRepository.getCurrentState()
        val trackId = currentState.currentTrack?.id ?: return
        
        val currentlyEnabled = silenceDetectionRepository.getSkipSilencePreference(trackId)
        silenceDetectionRepository.saveSkipSilencePreference(trackId, !currentlyEnabled)
    }

    suspend fun cycleAggressivenessForCurrentTrack() {
        val currentAggressiveness = getCurrentAggressiveness()
        val nextAggressiveness = when (currentAggressiveness) {
            SilenceAggressiveness.MILD -> SilenceAggressiveness.MEDIUM
            SilenceAggressiveness.MEDIUM -> SilenceAggressiveness.AGGRESSIVE
            SilenceAggressiveness.AGGRESSIVE -> SilenceAggressiveness.MILD
        }
        setAggressivenessForCurrentTrack(nextAggressiveness)
    }

    suspend fun getCurrentAggressiveness(): SilenceAggressiveness {
        val currentState = playerStateRepository.getCurrentState()
        val trackId = currentState.currentTrack?.id
        
        return if (trackId != null) {
            silenceDetectionRepository.getAggressivenessPreference(trackId)
        } else {
            silenceDetectionRepository.getGlobalAggressivenessPreference()
        }
    }

    suspend fun isCurrentlyEnabled(): Boolean {
        val currentState = playerStateRepository.getCurrentState()
        val trackId = currentState.currentTrack?.id
        
        return if (trackId != null) {
            silenceDetectionRepository.getSkipSilencePreference(trackId)
        } else {
            silenceDetectionRepository.getGlobalSkipSilencePreference()
        }
    }

    fun observeSkipSilenceState(): Flow<Pair<Boolean, SilenceAggressiveness>> {
        return combine(
            playerStateRepository.observePlayerState(),
            silenceDetectionRepository.observeSkipSilencePreferences()
        ) { playerState, _ ->
            val trackId = playerState.currentTrack?.id
            if (trackId != null) {
                val enabled = silenceDetectionRepository.getSkipSilencePreference(trackId)
                val aggressiveness = silenceDetectionRepository.getAggressivenessPreference(trackId)
                Pair(enabled, aggressiveness)
            } else {
                val enabled = silenceDetectionRepository.getGlobalSkipSilencePreference()
                val aggressiveness = silenceDetectionRepository.getGlobalAggressivenessPreference()
                Pair(enabled, aggressiveness)
            }
        }
    }

    suspend fun estimateTimeSaved(analysisResult: AudioAnalysisResult): Long {
        val config = getConfigForCurrentTrack() ?: return 0L
        val skippableSegments = analysisResult.getSkippableSegments(config)
        return skippableSegments.sumOf { it.durationMs }
    }
}