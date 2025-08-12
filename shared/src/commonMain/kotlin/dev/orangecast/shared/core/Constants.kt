package dev.orangecast.shared.core

object Constants {
    
    object Cache {
        const val SEARCH_RESULTS_DURATION_SECONDS = 600L
        const val PODCAST_DETAILS_DURATION_SECONDS = 3600L
    }
    
    object ImageResolution {
        const val HIGH_QUALITY = 600
        const val MEDIUM_QUALITY = 300
        const val LOW_QUALITY = 100
        const val THUMBNAIL = 60
        const val ICON = 30
    }
    
    object TextQuality {
        const val LONG_TEXT_THRESHOLD = 100
        const val MEDIUM_TEXT_THRESHOLD = 50
        const val LONG_TEXT_BONUS = 1000
        const val MEDIUM_TEXT_BONUS = 500
    }
    
    object PlaybackThresholds {
        const val NEAR_END_THRESHOLD_MS = 30000L
        const val COMPLETION_THRESHOLD_MS = 5000L
        const val COMPLETION_PERCENTAGE = 0.9f
    }
    
    object Animation {
        const val SPLASH_SCALE_DURATION_MS = 1000
        const val SPLASH_ALPHA_DURATION_MS = 800
        const val SHORT_DURATION_MS = 300
        const val MEDIUM_DURATION_MS = 500
    }
    
    object ApiLimits {
        const val ITUNES_RATE_LIMIT = 1000
        const val LISTEN_NOTES_FREE_LIMIT = 100
        const val LISTEN_NOTES_PRO_LIMIT = 10000
        const val PODCAST_INDEX_LIMIT = 500
    }
}