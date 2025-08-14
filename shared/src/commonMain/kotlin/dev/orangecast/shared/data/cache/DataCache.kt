package dev.orangecast.shared.data.cache

interface DataCache<K, V> {
    fun get(key: K): V?
    fun put(key: K, value: V, ttlMillis: Long = CacheEntry.DEFAULT_TTL)
    fun remove(key: K)
    fun clear()
    fun size(): Int
    fun containsKey(key: K): Boolean
}

class InMemoryDataCache<K, V>(
    private val maxSize: Int = DEFAULT_MAX_SIZE
) : DataCache<K, V> {
    
    private val cache = mutableMapOf<K, CacheEntry<V>>()
    
    override fun get(key: K): V? {
        val entry = cache[key]
        return if (entry != null && !entry.isExpired()) {
            entry.data
        } else {
            if (entry != null) {
                cache.remove(key) // Remove expired entry
            }
            null
        }
    }
    
    override fun put(key: K, value: V, ttlMillis: Long) {
        // Remove expired entries if we're at capacity
        if (cache.size >= maxSize) {
            cleanupExpired()
            // If still at capacity, remove oldest entry
            if (cache.size >= maxSize) {
                val oldestKey = cache.keys.first()
                cache.remove(oldestKey)
            }
        }
        
        cache[key] = CacheEntry(value, ttlMillis = ttlMillis)
    }
    
    override fun remove(key: K) {
        cache.remove(key)
    }
    
    override fun clear() {
        cache.clear()
    }
    
    override fun size(): Int = cache.size
    
    override fun containsKey(key: K): Boolean {
        val entry = cache[key]
        return entry != null && !entry.isExpired()
    }
    
    private fun cleanupExpired() {
        val expiredKeys = cache.filter { it.value.isExpired() }.keys
        expiredKeys.forEach { cache.remove(it) }
    }
    
    companion object {
        const val DEFAULT_MAX_SIZE = 500
    }
}