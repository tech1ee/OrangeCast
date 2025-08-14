package dev.orangecast.shared.data.cache

import androidx.compose.ui.graphics.ImageBitmap

interface ImageCacheManager {
    suspend fun getImage(url: String): ImageBitmap?
    suspend fun putImage(url: String, image: ImageBitmap)
    fun clearCache()
    fun getCacheSize(): Int
}

class InMemoryImageCacheManager(
    private val maxSize: Int = DEFAULT_MAX_SIZE
) : ImageCacheManager {
    
    private val imageCache = InMemoryDataCache<String, ImageBitmap>(maxSize)
    
    override suspend fun getImage(url: String): ImageBitmap? {
        return imageCache.get(url)
    }
    
    override suspend fun putImage(url: String, image: ImageBitmap) {
        imageCache.put(url, image, CacheEntry.IMAGE_TTL)
    }
    
    override fun clearCache() {
        imageCache.clear()
    }
    
    override fun getCacheSize(): Int {
        return imageCache.size()
    }
    
    companion object {
        private const val DEFAULT_MAX_SIZE = 200
    }
}