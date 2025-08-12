package dev.orangecast.shared.ui.accessibility

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import dev.orangecast.shared.domain.model.PlaybackState

fun Modifier.accessibleButton(
    description: String,
    enabled: Boolean = true,
    stateDescription: String? = null
): Modifier = this.semantics {
    role = Role.Button
    contentDescription = description
    if (!enabled) {
        this.stateDescription = "Disabled"
    } else if (stateDescription != null) {
        this.stateDescription = stateDescription
    }
}

fun Modifier.accessibleToggleButton(
    description: String,
    isToggled: Boolean,
    enabled: Boolean = true
): Modifier = this.semantics {
    role = Role.Button
    contentDescription = description
    toggleableState = if (isToggled) ToggleableState.On else ToggleableState.Off
    if (!enabled) {
        stateDescription = "Disabled"
    }
}

fun Modifier.accessiblePlaybackButton(
    playbackState: PlaybackState,
    isBuffering: Boolean = false,
    enabled: Boolean = true
): Modifier = this.semantics {
    role = Role.Button
    contentDescription = when {
        isBuffering -> "Buffering"
        playbackState.isPlaying -> AccessibilityStrings.PAUSE_BUTTON
        else -> AccessibilityStrings.PLAY_BUTTON
    }
    
    val state = when {
        !enabled -> "Disabled"
        isBuffering -> AccessibilityStrings.BUFFERING_STATE
        playbackState.isPlaying -> "Playing"
        else -> "Paused"
    }
    stateDescription = state
}

fun Modifier.accessibleSlider(
    description: String,
    valueDescription: String,
    enabled: Boolean = true
): Modifier = this.semantics {
    contentDescription = description
    stateDescription = if (enabled) valueDescription else "Disabled"
}

fun Modifier.accessibleHeading(
    level: Int = 1
): Modifier = this.semantics {
    heading()
}

fun Modifier.accessibleImage(
    description: String
): Modifier = this.semantics {
    role = Role.Image
    contentDescription = description
}

fun Modifier.accessiblePodcastItem(
    title: String,
    author: String,
    isSubscribed: Boolean = false,
    hasNewEpisodes: Boolean = false
): Modifier = this.semantics {
    role = Role.Button
    contentDescription = AccessibilityStrings.podcastItemDescription(title, author)
    
    val statusParts = mutableListOf<String>()
    if (isSubscribed) statusParts.add("Subscribed")
    if (hasNewEpisodes) statusParts.add("Has new episodes")
    
    if (statusParts.isNotEmpty()) {
        stateDescription = statusParts.joinToString(", ")
    }
}

fun Modifier.accessibleEpisodeItem(
    title: String,
    podcastName: String,
    duration: String,
    isPlaying: Boolean = false,
    isDownloaded: Boolean = false,
    progress: Float = 0f
): Modifier = this.semantics {
    role = Role.Button
    contentDescription = AccessibilityStrings.episodeItemDescription(title, podcastName, duration)
    
    val statusParts = mutableListOf<String>()
    if (isPlaying) statusParts.add(AccessibilityStrings.NOW_PLAYING_INDICATOR)
    if (isDownloaded) statusParts.add(AccessibilityStrings.DOWNLOADED_INDICATOR)
    if (progress > 0f && progress < 1f) {
        statusParts.add("${(progress * 100).toInt()}% completed")
    } else if (progress >= 1f) {
        statusParts.add(AccessibilityStrings.COMPLETED_INDICATOR)
    }
    
    if (statusParts.isNotEmpty()) {
        stateDescription = statusParts.joinToString(", ")
    }
}

fun Modifier.accessibleProgress(
    current: String,
    total: String,
    progress: Float
): Modifier = this.semantics {
    contentDescription = AccessibilityStrings.progressDescription(current, total)
    stateDescription = "${(progress * 100).toInt()}% complete"
}

fun Modifier.decorativeElement(): Modifier = this.clearAndSetSemantics { }

fun Modifier.accessibleLoadingState(
    description: String = AccessibilityStrings.LOADING_STATE
): Modifier = this.semantics {
    contentDescription = description
    stateDescription = "Loading"
}

fun Modifier.accessibleErrorState(
    description: String = AccessibilityStrings.ERROR_STATE,
    errorMessage: String? = null
): Modifier = this.semantics {
    contentDescription = description
    stateDescription = errorMessage ?: "Error occurred"
}

fun Modifier.accessibleEmptyState(
    description: String = AccessibilityStrings.EMPTY_STATE
): Modifier = this.semantics {
    contentDescription = description
    stateDescription = "No content"
}

fun formatDurationForAccessibility(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    
    return when {
        hours > 0 -> "$hours hours, $minutes minutes, $seconds seconds"
        minutes > 0 -> "$minutes minutes, $seconds seconds"
        else -> "$seconds seconds"
    }
}

fun formatPositionForAccessibility(positionMs: Long, durationMs: Long): String {
    val currentFormatted = formatDurationForAccessibility(positionMs)
    val totalFormatted = formatDurationForAccessibility(durationMs)
    val progress = if (durationMs > 0) (positionMs.toFloat() / durationMs * 100).toInt() else 0
    
    return "Position: $currentFormatted of $totalFormatted, $progress% complete"
}