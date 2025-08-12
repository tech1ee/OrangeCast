package dev.orangecast.shared.domain.model

enum class PlaybackSpeed(val value: Float, val displayText: String) {
    SPEED_0_5X(0.5f, "0.5×"),
    SPEED_0_75X(0.75f, "0.75×"),
    SPEED_1X(1.0f, "1×"),
    SPEED_1_25X(1.25f, "1.25×"),
    SPEED_1_5X(1.5f, "1.5×"),
    SPEED_1_75X(1.75f, "1.75×"),
    SPEED_2X(2.0f, "2×"),
    SPEED_2_5X(2.5f, "2.5×"),
    SPEED_3X(3.0f, "3×");

    companion object {
        fun fromValue(value: Float): PlaybackSpeed {
            return values().find { it.value == value } ?: SPEED_1X
        }

        fun getNextSpeed(currentSpeed: PlaybackSpeed): PlaybackSpeed {
            val currentIndex = values().indexOf(currentSpeed)
            return if (currentIndex < values().size - 1) {
                values()[currentIndex + 1]
            } else {
                values()[0]
            }
        }

        fun getPreviousSpeed(currentSpeed: PlaybackSpeed): PlaybackSpeed {
            val currentIndex = values().indexOf(currentSpeed)
            return if (currentIndex > 0) {
                values()[currentIndex - 1]
            } else {
                values()[values().size - 1]
            }
        }

        fun getAvailableSpeeds(): List<PlaybackSpeed> = values().toList()

        fun getCommonSpeeds(): List<PlaybackSpeed> = listOf(
            SPEED_0_75X,
            SPEED_1X,
            SPEED_1_25X,
            SPEED_1_5X,
            SPEED_2X
        )
    }

    fun isSlowerThanNormal(): Boolean = value < 1.0f
    fun isFasterThanNormal(): Boolean = value > 1.0f
    fun isNormalSpeed(): Boolean = value == 1.0f
}