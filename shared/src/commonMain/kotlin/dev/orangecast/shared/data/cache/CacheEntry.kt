package dev.orangecast.shared.data.cache

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class CacheEntry<T>(
    val data: T,
    val timestamp: Instant = Clock.System.now(),
    val ttlMillis: Long = DEFAULT_TTL
) {
    fun isExpired(): Boolean {
        val now = Clock.System.now()
        return (now.toEpochMilliseconds() - timestamp.toEpochMilliseconds()) > ttlMillis
    }
    
    companion object {
        const val DEFAULT_TTL = 5 * 60 * 1000L // 5 minutes
        const val SEARCH_TTL = 10 * 60 * 1000L // 10 minutes  
        const val FEATURED_TTL = 30 * 60 * 1000L // 30 minutes
        const val EPISODE_TTL = 60 * 60 * 1000L // 1 hour
        const val IMAGE_TTL = 24 * 60 * 60 * 1000L // 24 hours
        const val SUBSCRIPTION_TTL = Long.MAX_VALUE // Never expires
    }
}