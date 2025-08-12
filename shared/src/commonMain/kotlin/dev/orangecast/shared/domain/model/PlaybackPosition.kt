package dev.orangecast.shared.domain.model

import kotlinx.datetime.Instant

data class PlaybackPosition(
    val episodeId: String,
    val position: Long,
    val duration: Long,
    val lastUpdated: Instant,
    val isCompleted: Boolean = false
) {
    val progressPercentage: Float
        get() = if (duration > 0) (position.toFloat() / duration.toFloat()) else 0f
    
    val isNearEnd: Boolean
        get() = duration > 0 && (duration - position) < 30000
    
    val shouldMarkAsPlayed: Boolean
        get() = isCompleted || progressPercentage >= 0.9f || isNearEnd
    
    fun withPosition(newPosition: Long): PlaybackPosition {
        return copy(
            position = newPosition.coerceIn(0L, duration),
            lastUpdated = kotlinx.datetime.Clock.System.now(),
            isCompleted = newPosition >= duration || (duration > 0 && newPosition >= duration - 5000)
        )
    }
    
    companion object {
        fun create(episodeId: String, position: Long, duration: Long): PlaybackPosition {
            return PlaybackPosition(
                episodeId = episodeId,
                position = position.coerceIn(0L, duration),
                duration = duration,
                lastUpdated = kotlinx.datetime.Clock.System.now(),
                isCompleted = position >= duration || (duration > 0 && position >= duration - 5000)
            )
        }
    }
}