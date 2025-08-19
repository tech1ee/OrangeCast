package dev.orangecast.shared.data.config

object AppConfig {
    object Cache {
        const val SEARCH_CACHE_SIZE = 100
        const val FEATURED_CACHE_SIZE = 20
        const val CATEGORY_CACHE_SIZE = 50
        const val DETAILS_CACHE_SIZE = 200
        const val EPISODES_CACHE_SIZE = 100
        const val DEFAULT_MAX_SIZE = 500
        
        const val DEFAULT_TTL = 5 * 60 * 1000L
        const val SEARCH_TTL = 10 * 60 * 1000L
        const val FEATURED_TTL = 30 * 60 * 1000L
        const val EPISODE_TTL = 60 * 60 * 1000L
        const val IMAGE_TTL = 24 * 60 * 60 * 1000L
    }
    
    object Network {
        const val HTTP_SEARCH_CACHE_SECONDS = 600
        const val HTTP_LOOKUP_CACHE_SECONDS = 3600
        const val MAX_RETRIES = 3
        const val SERVER_ERROR_THRESHOLD = 500
    }
    
    object RSS {
        const val MAX_FEED_SIZE_BYTES = 50_000_000L
        const val MAX_EPISODES_PARSED = 50
        const val SIZE_ERROR_DIVIDER = 1_000_000L
    }
    
    object API {
        const val DEFAULT_SEARCH_LIMIT = 50
        const val FEATURED_PODCASTS_PER_CATEGORY = 4
        const val FEATURED_PODCASTS_TOTAL = 20
        const val CATEGORY_SEARCH_LIMIT = 15
        const val ALTERNATIVE_FEED_SEARCH_LIMIT = 10
    }
    
    object UI {
        const val SHIMMER_DURATION_MS = 1200
        const val SUBSCRIPTION_TOGGLE_DELAY_MS = 300L
    }
}