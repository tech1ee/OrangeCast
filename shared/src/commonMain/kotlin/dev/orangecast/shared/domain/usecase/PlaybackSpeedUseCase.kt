package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.PlaybackSpeed
import dev.orangecast.shared.domain.repository.PlaybackSpeedRepository
import dev.orangecast.shared.domain.repository.PlayerStateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class PlaybackSpeedUseCase(
    private val playbackSpeedRepository: PlaybackSpeedRepository,
    private val playerStateRepository: PlayerStateRepository
) {

    suspend fun setSpeedForCurrentTrack(speed: PlaybackSpeed) {
        val currentState = playerStateRepository.getCurrentState()
        val currentTrack = currentState.currentTrack

        if (currentTrack != null) {
            playbackSpeedRepository.savePreferredSpeed(currentTrack.id, speed)
        } else {
            playbackSpeedRepository.saveGlobalPreferredSpeed(speed)
        }

        playerStateRepository.updateSpeed(speed.value)
    }

    suspend fun setGlobalSpeed(speed: PlaybackSpeed) {
        playbackSpeedRepository.saveGlobalPreferredSpeed(speed)
        playerStateRepository.updateSpeed(speed.value)
    }

    suspend fun getSpeedForTrack(trackId: String): PlaybackSpeed {
        return playbackSpeedRepository.getPreferredSpeed(trackId)
    }

    suspend fun getCurrentSpeed(): PlaybackSpeed {
        val currentState = playerStateRepository.getCurrentState()
        return PlaybackSpeed.fromValue(currentState.playbackSpeed)
    }

    suspend fun increaseSpeed() {
        val currentSpeed = getCurrentSpeed()
        val nextSpeed = PlaybackSpeed.getNextSpeed(currentSpeed)
        setSpeedForCurrentTrack(nextSpeed)
    }

    suspend fun decreaseSpeed() {
        val currentSpeed = getCurrentSpeed()
        val previousSpeed = PlaybackSpeed.getPreviousSpeed(currentSpeed)
        setSpeedForCurrentTrack(previousSpeed)
    }

    suspend fun resetToNormalSpeed() {
        setSpeedForCurrentTrack(PlaybackSpeed.SPEED_1X)
    }

    suspend fun applySpeedForTrack(trackId: String) {
        val preferredSpeed = playbackSpeedRepository.getPreferredSpeed(trackId)
        playerStateRepository.updateSpeed(preferredSpeed.value)
    }

    fun observeSpeedForCurrentTrack(): Flow<PlaybackSpeed> {
        return combine(
            playerStateRepository.observePlayerState(),
            playbackSpeedRepository.observeSpeedPreferences()
        ) { playerState, _ ->
            PlaybackSpeed.fromValue(playerState.playbackSpeed)
        }
    }

    suspend fun getAvailableSpeeds(): List<PlaybackSpeed> {
        return PlaybackSpeed.getAvailableSpeeds()
    }

    suspend fun getCommonSpeeds(): List<PlaybackSpeed> {
        return PlaybackSpeed.getCommonSpeeds()
    }

    suspend fun isSpeedCustomizedForTrack(trackId: String): Boolean {
        val preferredSpeed = playbackSpeedRepository.getPreferredSpeed(trackId)
        val globalSpeed = playbackSpeedRepository.getGlobalPreferredSpeed()
        return preferredSpeed != globalSpeed
    }
}