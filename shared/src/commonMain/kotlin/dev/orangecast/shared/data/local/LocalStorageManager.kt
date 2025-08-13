package dev.orangecast.shared.data.local

import dev.orangecast.shared.domain.model.Podcast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * LocalStorageManager handles local storage of podcast subscriptions
 * Uses in-memory storage for now, but can be extended to use platform-specific persistence
 */
class LocalStorageManager {
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    // In-memory storage for subscriptions (will be replaced with persistent storage)
    private val _subscribedPodcasts = MutableStateFlow<Set<String>>(emptySet())
    private val _podcastCache = MutableStateFlow<Map<String, Podcast>>(emptyMap())
    
    init {
        loadSubscriptions()
    }
    
    /**
     * Get all subscribed podcast IDs
     */
    suspend fun getSubscribedPodcastIds(): Set<String> {
        return _subscribedPodcasts.value
    }
    
    /**
     * Get all subscribed podcasts as Flow
     */
    fun getSubscribedPodcastsFlow(): Flow<List<Podcast>> {
        return _subscribedPodcasts.asStateFlow().map { subscribedIds ->
            subscribedIds.mapNotNull { id -> _podcastCache.value[id] }
        }
    }
    
    /**
     * Check if a podcast is subscribed
     */
    suspend fun isSubscribed(podcastId: String): Boolean {
        return _subscribedPodcasts.value.contains(podcastId)
    }
    
    /**
     * Subscribe to a podcast
     */
    suspend fun subscribeToPodcast(podcast: Podcast) {
        val currentSubscriptions = _subscribedPodcasts.value.toMutableSet()
        val currentCache = _podcastCache.value.toMutableMap()
        
        currentSubscriptions.add(podcast.id)
        currentCache[podcast.id] = podcast
        
        _subscribedPodcasts.value = currentSubscriptions
        _podcastCache.value = currentCache
        
        saveSubscriptions()
    }
    
    /**
     * Unsubscribe from a podcast
     */
    suspend fun unsubscribeFromPodcast(podcastId: String) {
        val currentSubscriptions = _subscribedPodcasts.value.toMutableSet()
        val currentCache = _podcastCache.value.toMutableMap()
        
        currentSubscriptions.remove(podcastId)
        currentCache.remove(podcastId)
        
        _subscribedPodcasts.value = currentSubscriptions
        _podcastCache.value = currentCache
        
        saveSubscriptions()
    }
    
    /**
     * Get subscribed podcasts from cache
     */
    suspend fun getSubscribedPodcasts(): List<Podcast> {
        val subscribedIds = _subscribedPodcasts.value
        val cache = _podcastCache.value
        
        return subscribedIds.mapNotNull { id -> cache[id] }
    }
    
    private fun loadSubscriptions() {
        
        // Sample data for demonstration - will be replaced with real persistence
        val samplePodcasts = mapOf(
            "1565853546" to Podcast(
                id = "1565853546",
                title = "SmartLess",
                description = "New episodes every Monday",
                imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/Podcasts115/v4/6a/c6/70/6ac67066-99b2-e73b-a4e9-5a1d50e5da10/mza_17169275509651261055.jpg/600x600bb.jpg",
                author = "Jason Bateman, Sean Hayes, Will Arnett",
                category = "Comedy",
                language = "en",
                isExplicit = false,
                episodeCount = 100,
                lastUpdated = System.currentTimeMillis()
            ),
            "1441923632" to Podcast(
                id = "1441923632", 
                title = "Wait Wait... Don't Tell Me!",
                description = "NPR's news quiz show",
                imageUrl = "https://media.npr.org/assets/img/2018/08/03/wait-wait-logo_sq-cb2de67dc0ad8a84df9cea7f9dd2a4e9ebc2e21a.jpg",
                author = "NPR",
                category = "Comedy",
                language = "en",
                isExplicit = false,
                episodeCount = 200,
                lastUpdated = System.currentTimeMillis()
            )
        )
        
        _subscribedPodcasts.value = samplePodcasts.keys
        _podcastCache.value = samplePodcasts
    }
    
    private fun saveSubscriptions() {
        // In-memory storage active
    }
    
    /**
     * Clear all subscriptions (for testing/debugging)
     */
    suspend fun clearAllSubscriptions() {
        _subscribedPodcasts.value = emptySet()
        _podcastCache.value = emptyMap()
        saveSubscriptions()
    }
}