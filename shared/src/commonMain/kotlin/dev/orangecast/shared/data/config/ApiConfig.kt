package dev.orangecast.shared.data.config

object ApiConfig {
    // ListenNotes API
    const val LISTENNOTES_BASE_URL = "https://listen-api.listennotes.com/api/v2"
    const val LISTENNOTES_SEARCH_URL = "$LISTENNOTES_BASE_URL/search"
    const val LISTENNOTES_BEST_PODCASTS_URL = "$LISTENNOTES_BASE_URL/best_podcasts"
    const val LISTENNOTES_GENRES_URL = "$LISTENNOTES_BASE_URL/genres"
    
    
    const val PODCASTINDEX_BASE_URL = "https://api.podcastindex.org/api/1.0"
    const val PODCASTINDEX_SEARCH_URL = "$PODCASTINDEX_BASE_URL/search/byterm"
    const val PODCASTINDEX_LOOKUP_URL = "$PODCASTINDEX_BASE_URL/podcasts/byfeedid" 
    const val PODCASTINDEX_SEARCH_TITLE_URL = "$PODCASTINDEX_BASE_URL/search/bytitle"
    
    const val USER_AGENT = "OrangeCast/1.0"
    
}