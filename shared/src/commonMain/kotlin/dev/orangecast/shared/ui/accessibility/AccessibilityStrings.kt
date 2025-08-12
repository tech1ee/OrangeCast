package dev.orangecast.shared.ui.accessibility

object AccessibilityStrings {
    
    const val PLAY_BUTTON = "Play episode"
    const val PAUSE_BUTTON = "Pause episode"
    const val SKIP_NEXT_BUTTON = "Skip to next episode"
    const val SKIP_PREVIOUS_BUTTON = "Skip to previous episode"
    const val SEEK_FORWARD_BUTTON = "Skip forward 30 seconds"
    const val SEEK_BACKWARD_BUTTON = "Skip backward 30 seconds"
    const val PLAYBACK_SPEED_BUTTON = "Change playback speed"
    const val VOLUME_SLIDER = "Volume control"
    const val PROGRESS_SLIDER = "Episode progress"
    const val PROGRESS_INDICATOR = "Episode playback progress"
    
    const val DISCOVER_TAB = "Discover new podcasts"
    const val SEARCH_TAB = "Search for podcasts"
    const val LIBRARY_TAB = "Your podcast library"
    const val BACK_BUTTON = "Go back"
    const val CLOSE_BUTTON = "Close"
    
    const val PODCAST_ITEM = "Podcast"
    const val EPISODE_ITEM = "Episode"
    const val SUBSCRIBE_BUTTON = "Subscribe to podcast"
    const val UNSUBSCRIBE_BUTTON = "Unsubscribe from podcast"
    const val DOWNLOAD_BUTTON = "Download episode"
    const val REMOVE_DOWNLOAD_BUTTON = "Remove download"
    const val EPISODE_PLAY_BUTTON = "Play episode"
    const val EPISODE_PAUSE_BUTTON = "Pause episode"
    
    const val SEARCH_FIELD = "Search for podcasts and episodes"
    const val CLEAR_SEARCH_BUTTON = "Clear search"
    const val SEARCH_RESULT = "Search result"
    
    const val LOADING_STATE = "Loading content"
    const val ERROR_STATE = "Error loading content"
    const val EMPTY_STATE = "No content available"
    const val BUFFERING_STATE = "Buffering audio"
    
    const val THEME_SELECTOR = "Choose app theme"
    const val LIGHT_THEME_OPTION = "Light theme"
    const val DARK_THEME_OPTION = "Dark theme"
    const val SYSTEM_THEME_OPTION = "Follow system theme"
    
    const val DURATION_FORMAT = "Duration: %s"
    const val POSITION_FORMAT = "Current position: %s"
    const val REMAINING_TIME_FORMAT = "Time remaining: %s"
    const val EPISODE_COUNT_FORMAT = "%d episodes"
    
    const val QUEUE_BUTTON = "View playback queue"
    const val REMOVE_FROM_QUEUE = "Remove from queue"
    const val MOVE_UP_IN_QUEUE = "Move up in queue"
    const val MOVE_DOWN_IN_QUEUE = "Move down in queue"
    const val SHUFFLE_BUTTON = "Shuffle queue"
    const val REPEAT_BUTTON = "Repeat mode"
    
    const val PODCAST_DESCRIPTION = "Podcast description"
    const val EPISODE_DESCRIPTION = "Episode description"
    const val PUBLICATION_DATE = "Published on %s"
    const val PODCAST_ARTWORK = "Podcast artwork"
    const val EPISODE_ARTWORK = "Episode artwork"
    
    const val DOWNLOAD_COMPLETE = "Download complete"
    const val DOWNLOAD_IN_PROGRESS = "Download in progress"
    const val DOWNLOAD_FAILED = "Download failed"
    const val DOWNLOADED_INDICATOR = "Downloaded"
    
    const val NOW_PLAYING_INDICATOR = "Currently playing"
    const val PAUSED_INDICATOR = "Paused"
    const val COMPLETED_INDICATOR = "Completed"
    const val IN_PROGRESS_INDICATOR = "In progress"
    
    fun podcastItemDescription(title: String, author: String): String {
        return "$PODCAST_ITEM: $title by $author. Tap to view details"
    }
    
    fun episodeItemDescription(title: String, podcast: String, duration: String): String {
        return "$EPISODE_ITEM: $title from $podcast. Duration: $duration. Tap to play"
    }
    
    fun progressDescription(current: String, total: String): String {
        return "Progress: $current of $total"
    }
    
    fun playbackSpeedDescription(speed: Float): String {
        return "Playback speed: ${speed}x"
    }
    
    fun subscriptionStatus(isSubscribed: Boolean): String {
        return if (isSubscribed) "Subscribed" else "Not subscribed"
    }
    
    fun downloadStatus(isDownloaded: Boolean, isDownloading: Boolean): String {
        return when {
            isDownloaded -> DOWNLOADED_INDICATOR
            isDownloading -> DOWNLOAD_IN_PROGRESS
            else -> "Not downloaded"
        }
    }
}