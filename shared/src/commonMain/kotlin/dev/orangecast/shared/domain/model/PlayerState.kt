package dev.orangecast.shared.domain.model

data class PlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentTrack: AudioTrack? = null,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.NONE,
    val volume: Float = 1.0f,
    val error: PlayerError? = null,
    val queue: List<AudioTrack> = emptyList(),
    val currentQueueIndex: Int = -1
) {
    val progress: Float
        get() = if (duration > 0) (currentPosition.toFloat() / duration.toFloat()) else 0f

    val hasNext: Boolean
        get() = when (repeatMode) {
            RepeatMode.NONE -> currentQueueIndex < queue.size - 1
            RepeatMode.ALL -> queue.isNotEmpty()
            RepeatMode.ONE -> true
        }

    val hasPrevious: Boolean
        get() = when (repeatMode) {
            RepeatMode.NONE -> currentQueueIndex > 0
            RepeatMode.ALL -> queue.isNotEmpty()
            RepeatMode.ONE -> true
        }

    fun withPlaybackState(isPlaying: Boolean, position: Long = currentPosition): PlayerState {
        return copy(isPlaying = isPlaying, currentPosition = position, isBuffering = false, error = null)
    }

    fun withBuffering(isBuffering: Boolean): PlayerState {
        return copy(isBuffering = isBuffering, error = null)
    }

    fun withPosition(position: Long): PlayerState {
        return copy(currentPosition = position.coerceIn(0L, duration))
    }

    fun withError(error: PlayerError?): PlayerState {
        return copy(error = error, isPlaying = false, isBuffering = false)
    }

    fun withSpeed(speed: Float): PlayerState {
        return copy(playbackSpeed = speed.coerceIn(0.5f, 3.0f))
    }

    fun withVolume(volume: Float): PlayerState {
        return copy(volume = volume.coerceIn(0.0f, 1.0f))
    }
}

enum class RepeatMode {
    NONE,
    ONE,
    ALL
}

data class PlayerError(
    val code: Int,
    val message: String,
    val cause: String? = null
) {
    companion object {
        fun networkError(message: String = "Network connection failed"): PlayerError {
            return PlayerError(1001, message, "NETWORK_ERROR")
        }

        fun mediaError(message: String = "Media playback failed"): PlayerError {
            return PlayerError(1002, message, "MEDIA_ERROR")
        }

        fun unknownError(message: String = "Unknown error occurred"): PlayerError {
            return PlayerError(1000, message, "UNKNOWN_ERROR")
        }
    }
}