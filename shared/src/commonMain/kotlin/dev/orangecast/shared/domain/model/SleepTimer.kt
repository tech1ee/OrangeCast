package dev.orangecast.shared.domain.model

data class SleepTimer(
    val durationMs: Long,
    val startTimeMs: Long,
    val isActive: Boolean,
    val fadeOutDurationMs: Long = 10000L, // 10 seconds fade-out
    val shouldPauseAtEndOfEpisode: Boolean = false
) {
    val remainingTimeMs: Long
        get() = if (isActive) {
            val elapsed = System.currentTimeMillis() - startTimeMs
            (durationMs - elapsed).coerceAtLeast(0L)
        } else 0L

    val progressPercentage: Float
        get() = if (isActive && durationMs > 0) {
            val elapsed = System.currentTimeMillis() - startTimeMs
            (elapsed.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val shouldStartFadeOut: Boolean
        get() = isActive && remainingTimeMs <= fadeOutDurationMs

    val fadeOutProgress: Float
        get() = if (shouldStartFadeOut) {
            1f - (remainingTimeMs.toFloat() / fadeOutDurationMs.toFloat())
        } else 0f

    val isExpired: Boolean
        get() = isActive && remainingTimeMs <= 0L

    companion object {
        fun create(durationMinutes: Int): SleepTimer {
            return SleepTimer(
                durationMs = durationMinutes * 60 * 1000L,
                startTimeMs = System.currentTimeMillis(),
                isActive = true
            )
        }

        fun createEndOfEpisode(): SleepTimer {
            return SleepTimer(
                durationMs = Long.MAX_VALUE,
                startTimeMs = System.currentTimeMillis(),
                isActive = true,
                shouldPauseAtEndOfEpisode = true
            )
        }

        fun getPresetDurations(): List<Pair<String, Int>> {
            return listOf(
                "5 minutes" to 5,
                "10 minutes" to 10,
                "15 minutes" to 15,
                "30 minutes" to 30,
                "45 minutes" to 45,
                "1 hour" to 60,
                "End of episode" to -1
            )
        }
    }

    fun extend(additionalMinutes: Int): SleepTimer {
        return copy(durationMs = durationMs + (additionalMinutes * 60 * 1000L))
    }

    fun cancel(): SleepTimer {
        return copy(isActive = false)
    }

    fun formatRemainingTime(): String {
        val remainingSeconds = (remainingTimeMs / 1000).toInt()
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        
        return if (minutes > 0) {
            "${minutes}m ${seconds}s"
        } else {
            "${seconds}s"
        }
    }
}

enum class SleepTimerState {
    INACTIVE,
    ACTIVE,
    FADING_OUT,
    EXPIRED
}

data class SleepTimerEvent(
    val type: SleepTimerEventType,
    val remainingTimeMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)

enum class SleepTimerEventType {
    STARTED,
    EXTENDED,
    FADE_OUT_STARTED,
    EXPIRED,
    CANCELLED
}