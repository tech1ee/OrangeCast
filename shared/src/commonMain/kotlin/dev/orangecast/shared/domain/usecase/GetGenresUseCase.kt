package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.data.api.ListenNotesApiService
import dev.orangecast.shared.data.api.model.ListenNotesGenre
import dev.orangecast.shared.data.cache.PodcastCacheManager
import dev.orangecast.shared.domain.model.Genre

class GetGenresUseCase(
    private val listenNotesApi: ListenNotesApiService,
    private val cacheManager: PodcastCacheManager
) {
    
    suspend fun execute(): Result<List<Genre>> {
        return try {
            // Check cache first
            val cachedGenres = cacheManager.getGenres()
            if (cachedGenres != null && cachedGenres.isNotEmpty()) {
                return Result.success(cachedGenres)
            }
            
            // Fetch from ListenNotes API
            val response = listenNotesApi.getGenres()
            val genres = response.genres.map { it.toDomainModel() }
            
            // Cache the results
            cacheManager.putGenres(genres)
            
            Result.success(genres)
        } catch (e: Exception) {
            // Return cached results if available
            val cachedGenres = cacheManager.getGenres()
            if (cachedGenres != null) {
                Result.success(cachedGenres)
            } else {
                Result.failure(e)
            }
        }
    }
    
    // Keep backward compatibility method
    suspend fun getGenres(): List<Genre> {
        return execute().getOrNull() ?: emptyList()
    }
    
    private fun ListenNotesGenre.toDomainModel(): Genre {
        return Genre(
            id = id,
            name = name,
            parentId = parentId
        )
    }
}