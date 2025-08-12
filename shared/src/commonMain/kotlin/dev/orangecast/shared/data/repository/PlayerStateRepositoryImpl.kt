package dev.orangecast.shared.data.repository

import dev.orangecast.shared.domain.model.AudioTrack
import dev.orangecast.shared.domain.model.PlayerError
import dev.orangecast.shared.domain.model.PlayerState
import dev.orangecast.shared.domain.model.RepeatMode
import dev.orangecast.shared.domain.repository.PlayerStateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PlayerStateRepositoryImpl : PlayerStateRepository {

    private val _playerState = MutableStateFlow(PlayerState())
    private val stateMutex = Mutex()
    
    private var recoveryState: PlayerState? = null

    override fun observePlayerState(): Flow<PlayerState> = _playerState.asStateFlow()

    override suspend fun getCurrentState(): PlayerState = _playerState.value

    override suspend fun updatePlaybackState(isPlaying: Boolean, position: Long) {
        stateMutex.withLock {
            val currentState = _playerState.value
            _playerState.value = currentState.withPlaybackState(isPlaying, position)
        }
    }

    override suspend fun updateBufferingState(isBuffering: Boolean) {
        stateMutex.withLock {
            val currentState = _playerState.value
            _playerState.value = currentState.withBuffering(isBuffering)
        }
    }

    override suspend fun updateCurrentTrack(track: AudioTrack?, duration: Long) {
        stateMutex.withLock {
            val currentState = _playerState.value
            _playerState.value = currentState.copy(
                currentTrack = track,
                duration = duration,
                currentPosition = 0L,
                error = null
            )
        }
    }

    override suspend fun updatePosition(position: Long) {
        stateMutex.withLock {
            val currentState = _playerState.value
            _playerState.value = currentState.withPosition(position)
        }
    }

    override suspend fun updateSpeed(speed: Float) {
        stateMutex.withLock {
            val currentState = _playerState.value
            _playerState.value = currentState.withSpeed(speed)
        }
    }

    override suspend fun updateVolume(volume: Float) {
        stateMutex.withLock {
            val currentState = _playerState.value
            _playerState.value = currentState.withVolume(volume)
        }
    }

    override suspend fun updateRepeatMode(mode: RepeatMode) {
        stateMutex.withLock {
            val currentState = _playerState.value
            _playerState.value = currentState.copy(repeatMode = mode)
        }
    }

    override suspend fun updateShuffleMode(enabled: Boolean) {
        stateMutex.withLock {
            val currentState = _playerState.value
            _playerState.value = currentState.copy(isShuffleEnabled = enabled)
        }
    }

    override suspend fun updateQueue(tracks: List<AudioTrack>, currentIndex: Int) {
        stateMutex.withLock {
            val currentState = _playerState.value
            val validIndex = currentIndex.coerceIn(0, (tracks.size - 1).coerceAtLeast(0))
            _playerState.value = currentState.copy(
                queue = tracks,
                currentQueueIndex = if (tracks.isEmpty()) -1 else validIndex,
                currentTrack = tracks.getOrNull(validIndex)
            )
        }
    }

    override suspend fun setError(error: PlayerError?) {
        stateMutex.withLock {
            val currentState = _playerState.value
            _playerState.value = currentState.withError(error)
        }
    }

    override suspend fun clearError() {
        setError(null)
    }

    override suspend fun saveStateForRecovery() {
        stateMutex.withLock {
            recoveryState = _playerState.value
        }
    }

    override suspend fun restoreStateFromRecovery(): PlayerState? {
        return stateMutex.withLock {
            recoveryState
        }
    }

    override suspend fun clearRecoveredState() {
        stateMutex.withLock {
            recoveryState = null
        }
    }
}