package dev.orangecast.shared.data.optimization

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ApiCacheManager<K, V> {
    private val cache = mutableMapOf<K, CacheEntry<V>>()
    private val mutex = Mutex()
    
    suspend fun get(key: K, ttlSeconds: Long = 300): V? {
        return mutex.withLock {
            val entry = cache[key] ?: return@withLock null
            val now = Clock.System.now()
            
            if (now > entry.expiryTime) {
                cache.remove(key)
                null
            } else {
                entry.value
            }
        }
    }
    
    suspend fun put(key: K, value: V, ttlSeconds: Long = 300) {
        mutex.withLock {
            val expiryTime = Clock.System.now() + ttlSeconds.seconds
            cache[key] = CacheEntry(value, expiryTime)
        }
    }
    
    suspend fun invalidate(key: K) {
        mutex.withLock {
            cache.remove(key)
        }
    }
    
    suspend fun clear() {
        mutex.withLock {
            cache.clear()
        }
    }
    
    suspend fun size(): Int {
        return mutex.withLock {
            cache.size
        }
    }
    
    suspend fun cleanExpired() {
        mutex.withLock {
            val now = Clock.System.now()
            val expiredKeys = cache.entries
                .filter { now > it.value.expiryTime }
                .map { it.key }
            
            expiredKeys.forEach { cache.remove(it) }
        }
    }
}

private data class CacheEntry<V>(
    val value: V,
    val expiryTime: Instant
)