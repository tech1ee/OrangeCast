package dev.orangecast.shared.domain.model

data class SeekConfiguration(
    val forwardIntervalMs: Long,
    val backwardIntervalMs: Long,
    val enableSmartRewind: Boolean,
    val smartRewindThresholdTaps: Int,
    val smartRewindMultiplier: Float,
    val enableHapticFeedback: Boolean,
    val enableProgressGestures: Boolean
) {
    companion object {
        fun createDefault(): SeekConfiguration {
            return SeekConfiguration(
                forwardIntervalMs = 30000L, // 30 seconds
                backwardIntervalMs = 15000L, // 15 seconds
                enableSmartRewind = true,
                smartRewindThresholdTaps = 2,
                smartRewindMultiplier = 2.0f,
                enableHapticFeedback = true,
                enableProgressGestures = true
            )
        }

        fun createCustom(
            forwardSeconds: Int,
            backwardSeconds: Int,
            smartRewind: Boolean = true,
            hapticFeedback: Boolean = true
        ): SeekConfiguration {
            return SeekConfiguration(
                forwardIntervalMs = forwardSeconds * 1000L,
                backwardIntervalMs = backwardSeconds * 1000L,
                enableSmartRewind = smartRewind,
                smartRewindThresholdTaps = 2,
                smartRewindMultiplier = 2.0f,
                enableHapticFeedback = hapticFeedback,
                enableProgressGestures = true
            )
        }

        fun getPresetConfigurations(): List<Pair<String, SeekConfiguration>> {
            return listOf(
                "Standard" to createCustom(30, 15),
                "Quick" to createCustom(15, 10),
                "Podcast Pro" to createCustom(45, 30),
                "Precise" to createCustom(10, 5, smartRewind = false)
            )
        }
    }

    fun getForwardIntervalSeconds(): Int = (forwardIntervalMs / 1000).toInt()
    fun getBackwardIntervalSeconds(): Int = (backwardIntervalMs / 1000).toInt()
}

data class SeekAction(
    val type: SeekActionType,
    val targetPositionMs: Long,
    val intervalMs: Long,
    val isSmartRewind: Boolean = false
)

enum class SeekActionType {
    FORWARD,
    BACKWARD,
    SMART_REWIND,
    POSITION_SET
}

data class SmartRewindState(
    val consecutiveTaps: Int = 0,
    val lastTapTime: Long = 0L,
    val tapTimeWindowMs: Long = 2000L // 2 seconds
) {
    fun shouldActivateSmartRewind(config: SeekConfiguration, currentTime: Long): Boolean {
        return config.enableSmartRewind &&
               consecutiveTaps >= config.smartRewindThresholdTaps &&
               (currentTime - lastTapTime) <= tapTimeWindowMs
    }

    fun incrementTaps(currentTime: Long): SmartRewindState {
        return if ((currentTime - lastTapTime) <= tapTimeWindowMs) {
            copy(consecutiveTaps = consecutiveTaps + 1, lastTapTime = currentTime)
        } else {
            copy(consecutiveTaps = 1, lastTapTime = currentTime)
        }
    }

    fun reset(): SmartRewindState {
        return copy(consecutiveTaps = 0, lastTapTime = 0L)
    }
}