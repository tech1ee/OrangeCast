package dev.orangecast.shared.data.cache

import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.domain.model.PodcastDetails
import dev.orangecast.shared.domain.model.PodcastEpisode

class PodcastCacheManager {
    
    // Search results cache: query -> list of podcasts
    private val searchCache = InMemoryDataCache<String, List<Podcast>>(maxSize = 100)
    
    // Featured podcasts cache
    private val featuredCache = InMemoryDataCache<String, List<Podcast>>(maxSize = 20)
    
    // Category podcasts cache: category -> list of podcasts  
    private val categoryCache = InMemoryDataCache<String, List<Podcast>>(maxSize = 50)
    
    // Individual podcast details cache: podcastId -> PodcastDetails
    private val detailsCache = InMemoryDataCache<String, PodcastDetails>(maxSize = 200)
    
    // Episodes cache: podcastId -> list of episodes
    private val episodesCache = InMemoryDataCache<String, List<PodcastEpisode>>(maxSize = 100)
    
    // Search Results
    fun getSearchResults(query: String): List<Podcast>? = searchCache.get(query)
    
    fun putSearchResults(query: String, podcasts: List<Podcast>) {
        searchCache.put(query, podcasts, CacheEntry.SEARCH_TTL)
    }
    
    // Featured Podcasts
    fun getFeaturedPodcasts(): List<Podcast>? = featuredCache.get(FEATURED_KEY)
    
    fun putFeaturedPodcasts(podcasts: List<Podcast>) {
        featuredCache.put(FEATURED_KEY, podcasts, CacheEntry.FEATURED_TTL)
    }
    
    // Category Podcasts
    fun getCategoryPodcasts(category: String): List<Podcast>? = categoryCache.get(category)
    
    fun putCategoryPodcasts(category: String, podcasts: List<Podcast>) {
        categoryCache.put(category, podcasts, CacheEntry.FEATURED_TTL)
    }
    
    // Podcast Details
    fun getPodcastDetails(podcastId: String): PodcastDetails? = detailsCache.get(podcastId)
    
    fun putPodcastDetails(podcastId: String, details: PodcastDetails) {
        detailsCache.put(podcastId, details, CacheEntry.EPISODE_TTL)
    }
    
    // Episodes
    fun getEpisodes(podcastId: String): List<PodcastEpisode>? = episodesCache.get(podcastId)
    
    fun putEpisodes(podcastId: String, episodes: List<PodcastEpisode>) {
        episodesCache.put(podcastId, episodes, CacheEntry.EPISODE_TTL)
    }
    
    // Cache management
    fun clearSearchCache() = searchCache.clear()
    
    fun clearAllCaches() {
        searchCache.clear()
        featuredCache.clear() 
        categoryCache.clear()
        detailsCache.clear()
        episodesCache.clear()
    }
    
    fun getCacheStats(): CacheStats {
        return CacheStats(
            searchCacheSize = searchCache.size(),
            featuredCacheSize = featuredCache.size(),
            categoryCacheSize = categoryCache.size(),
            detailsCacheSize = detailsCache.size(),
            episodesCacheSize = episodesCache.size()
        )
    }
    
    companion object {
        private const val FEATURED_KEY = "featured_podcasts"
    }
}

data class CacheStats(
    val searchCacheSize: Int,
    val featuredCacheSize: Int,
    val categoryCacheSize: Int,
    val detailsCacheSize: Int,
    val episodesCacheSize: Int
) {
    val totalCacheSize: Int get() = searchCacheSize + featuredCacheSize + categoryCacheSize + detailsCacheSize + episodesCacheSize
}