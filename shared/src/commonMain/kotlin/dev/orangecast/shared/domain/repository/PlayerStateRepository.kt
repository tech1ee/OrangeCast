package dev.orangecast.shared.domain.repository

import dev.orangecast.shared.domain.model.AudioTrack
import dev.orangecast.shared.domain.model.PlayerState
import dev.orangecast.shared.domain.model.RepeatMode
import kotlinx.coroutines.flow.Flow

interface PlayerStateRepository {
    fun observePlayerState(): Flow<PlayerState>
    suspend fun getCurrentState(): PlayerState
    suspend fun updatePlaybackState(isPlaying: Boolean, position: Long = 0L)
    suspend fun updateBufferingState(isBuffering: Boolean)
    suspend fun updateCurrentTrack(track: AudioTrack?, duration: Long = 0L)
    suspend fun updatePosition(position: Long)
    suspend fun updateSpeed(speed: Float)
    suspend fun updateVolume(volume: Float)
    suspend fun updateRepeatMode(mode: RepeatMode)
    suspend fun updateShuffleMode(enabled: Boolean)
    suspend fun updateQueue(tracks: List<AudioTrack>, currentIndex: Int = 0)
    suspend fun setError(error: dev.orangecast.shared.domain.model.PlayerError?)
    suspend fun clearError()
    suspend fun saveStateForRecovery()
    suspend fun restoreStateFromRecovery(): PlayerState?
    suspend fun clearRecoveredState()
}