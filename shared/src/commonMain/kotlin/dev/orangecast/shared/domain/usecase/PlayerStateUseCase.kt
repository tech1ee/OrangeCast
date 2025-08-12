package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.AudioTrack
import dev.orangecast.shared.domain.model.PlayerError
import dev.orangecast.shared.domain.model.PlayerState
import dev.orangecast.shared.domain.model.RepeatMode
import dev.orangecast.shared.domain.repository.PlayerStateRepository
import kotlinx.coroutines.flow.Flow

class PlayerStateUseCase(
    private val playerStateRepository: PlayerStateRepository
) {

    fun observePlayerState(): Flow<PlayerState> {
        return playerStateRepository.observePlayerState()
    }

    suspend fun getCurrentState(): PlayerState {
        return playerStateRepository.getCurrentState()
    }

    suspend fun play(position: Long = 0L) {
        playerStateRepository.updatePlaybackState(isPlaying = true, position = position)
    }

    suspend fun pause(position: Long = 0L) {
        playerStateRepository.updatePlaybackState(isPlaying = false, position = position)
    }

    suspend fun stop() {
        val currentState = playerStateRepository.getCurrentState()
        playerStateRepository.updatePlaybackState(isPlaying = false, position = 0L)
        playerStateRepository.clearError()
    }

    suspend fun startBuffering() {
        playerStateRepository.updateBufferingState(isBuffering = true)
    }

    suspend fun stopBuffering() {
        playerStateRepository.updateBufferingState(isBuffering = false)
    }

    suspend fun loadTrack(track: AudioTrack, duration: Long = 0L) {
        playerStateRepository.updateCurrentTrack(track, duration)
        playerStateRepository.clearError()
    }

    suspend fun seekTo(position: Long) {
        val currentState = playerStateRepository.getCurrentState()
        val validPosition = position.coerceIn(0L, currentState.duration)
        playerStateRepository.updatePosition(validPosition)
    }

    suspend fun skipForward(intervalMs: Long = 30000L) {
        val currentState = playerStateRepository.getCurrentState()
        val newPosition = (currentState.currentPosition + intervalMs).coerceAtMost(currentState.duration)
        playerStateRepository.updatePosition(newPosition)
    }

    suspend fun skipBackward(intervalMs: Long = 15000L) {
        val currentState = playerStateRepository.getCurrentState()
        val newPosition = (currentState.currentPosition - intervalMs).coerceAtLeast(0L)
        playerStateRepository.updatePosition(newPosition)
    }

    suspend fun changeSpeed(speed: Float) {
        val validSpeed = speed.coerceIn(0.5f, 3.0f)
        playerStateRepository.updateSpeed(validSpeed)
    }

    suspend fun changeVolume(volume: Float) {
        val validVolume = volume.coerceIn(0.0f, 1.0f)
        playerStateRepository.updateVolume(validVolume)
    }

    suspend fun toggleRepeatMode() {
        val currentState = playerStateRepository.getCurrentState()
        val newMode = when (currentState.repeatMode) {
            RepeatMode.NONE -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.NONE
        }
        playerStateRepository.updateRepeatMode(newMode)
    }

    suspend fun toggleShuffle() {
        val currentState = playerStateRepository.getCurrentState()
        playerStateRepository.updateShuffleMode(!currentState.isShuffleEnabled)
    }

    suspend fun playNext(): Boolean {
        val currentState = playerStateRepository.getCurrentState()
        
        if (!currentState.hasNext) {
            return false
        }

        val nextIndex = when (currentState.repeatMode) {
            RepeatMode.ONE -> currentState.currentQueueIndex
            RepeatMode.ALL -> (currentState.currentQueueIndex + 1) % currentState.queue.size
            RepeatMode.NONE -> currentState.currentQueueIndex + 1
        }

        if (nextIndex < currentState.queue.size) {
            val nextTrack = currentState.queue[nextIndex]
            playerStateRepository.updateQueue(currentState.queue, nextIndex)
            loadTrack(nextTrack)
            return true
        }

        return false
    }

    suspend fun playPrevious(): Boolean {
        val currentState = playerStateRepository.getCurrentState()
        
        if (!currentState.hasPrevious) {
            return false
        }

        if (currentState.currentPosition > 5000L) {
            seekTo(0L)
            return true
        }

        val previousIndex = when (currentState.repeatMode) {
            RepeatMode.ONE -> currentState.currentQueueIndex
            RepeatMode.ALL -> {
                val prevIndex = currentState.currentQueueIndex - 1
                if (prevIndex < 0) currentState.queue.size - 1 else prevIndex
            }
            RepeatMode.NONE -> currentState.currentQueueIndex - 1
        }

        if (previousIndex >= 0 && previousIndex < currentState.queue.size) {
            val previousTrack = currentState.queue[previousIndex]
            playerStateRepository.updateQueue(currentState.queue, previousIndex)
            loadTrack(previousTrack)
            return true
        }

        return false
    }

    suspend fun setQueue(tracks: List<AudioTrack>, startIndex: Int = 0) {
        if (tracks.isEmpty()) {
            playerStateRepository.updateQueue(emptyList(), -1)
            playerStateRepository.updateCurrentTrack(null)
            return
        }

        val validStartIndex = startIndex.coerceIn(0, tracks.size - 1)
        playerStateRepository.updateQueue(tracks, validStartIndex)
        
        val startTrack = tracks[validStartIndex]
        loadTrack(startTrack)
    }

    suspend fun addToQueue(track: AudioTrack) {
        val currentState = playerStateRepository.getCurrentState()
        val newQueue = currentState.queue + track
        playerStateRepository.updateQueue(newQueue, currentState.currentQueueIndex)
    }

    suspend fun removeFromQueue(index: Int) {
        val currentState = playerStateRepository.getCurrentState()
        if (index < 0 || index >= currentState.queue.size) return

        val newQueue = currentState.queue.toMutableList()
        newQueue.removeAt(index)

        val newCurrentIndex = when {
            index < currentState.currentQueueIndex -> currentState.currentQueueIndex - 1
            index == currentState.currentQueueIndex -> {
                if (newQueue.isEmpty()) -1 else currentState.currentQueueIndex.coerceAtMost(newQueue.size - 1)
            }
            else -> currentState.currentQueueIndex
        }

        playerStateRepository.updateQueue(newQueue, newCurrentIndex)
        
        if (index == currentState.currentQueueIndex) {
            val newCurrentTrack = newQueue.getOrNull(newCurrentIndex)
            if (newCurrentTrack != null) {
                loadTrack(newCurrentTrack)
            } else {
                playerStateRepository.updateCurrentTrack(null)
            }
        }
    }

    suspend fun reportError(error: PlayerError) {
        playerStateRepository.setError(error)
        playerStateRepository.updatePlaybackState(isPlaying = false)
    }

    suspend fun clearError() {
        playerStateRepository.clearError()
    }

    suspend fun saveCurrentState() {
        playerStateRepository.saveStateForRecovery()
    }

    suspend fun restoreLastState(): PlayerState? {
        return playerStateRepository.restoreStateFromRecovery()
    }
}