package dev.orangecast.shared.ui.strings

object UiStrings {
    
    object Error {
        const val UNKNOWN_ERROR = "Something went wrong"
        const val NETWORK_ERROR = "Check your connection and try again"
        const val LOADING_FAILED = "Failed to load content"
        const val NO_DATA = "No content available"
        const val PODCAST_LOAD_FAILED = "Failed to load podcasts"
        const val EPISODE_LOAD_FAILED = "Failed to load episodes"
    }
    
    object Empty {
        const val NO_PODCASTS = "No podcasts available"
        const val NO_EPISODES = "No episodes available"
        const val NO_SEARCH_RESULTS = "No results found"
        const val NO_SUBSCRIPTIONS = "No subscriptions yet"
        const val NO_FAVORITES = "No favorites yet"
        const val SEARCH_PLACEHOLDER = "Start typing to search for podcasts"
    }
    
    object Actions {
        const val TRY_AGAIN = "Try Again"
        const val REFRESH = "Refresh"
        const val RETRY = "Retry"
        const val SEARCH = "Search"
        const val SUBSCRIBE = "Subscribe"
        const val UNSUBSCRIBE = "Unsubscribe"
        const val PLAY = "Play"
        const val PAUSE = "Pause"
        const val BACK = "Back"
    }
    
    object Navigation {
        const val DISCOVER = "Discover"
        const val SEARCH = "Search"
        const val LIBRARY = "Library"
        const val FAVORITES = "Favorites"
        const val SETTINGS = "Settings"
    }
    
    object Accessibility {
        const val ERROR_MESSAGE_PREFIX = "Error message: "
        const val BUTTON_PREFIX = " button"
        const val HEADING_SUFFIX = " heading"
        const val PODCAST_ITEM_DESCRIPTION = "Podcast item"
        const val EPISODE_ITEM_DESCRIPTION = "Episode item"
        const val NAVIGATION_DESCRIPTION = "Navigate to "
    }
}