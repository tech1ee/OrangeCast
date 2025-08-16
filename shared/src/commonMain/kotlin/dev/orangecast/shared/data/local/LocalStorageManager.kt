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
        // Start with empty subscriptions - users will add their own
        // This could be extended to load from platform-specific persistent storage
        _subscribedPodcasts.value = emptySet()
        _podcastCache.value = emptyMap()
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