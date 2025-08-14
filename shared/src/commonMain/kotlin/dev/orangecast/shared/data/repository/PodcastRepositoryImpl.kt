package dev.orangecast.shared.data.repository

import dev.orangecast.shared.data.api.ITunesApiService
import dev.orangecast.shared.data.api.model.ITunesPodcast
import dev.orangecast.shared.data.cache.PodcastCacheManager
import dev.orangecast.shared.data.local.LocalStorageManager
import dev.orangecast.shared.data.rss.RssFeedParser
import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.domain.model.PodcastDetails
import dev.orangecast.shared.domain.model.PodcastEpisode
import dev.orangecast.shared.domain.repository.PodcastRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf

class PodcastRepositoryImpl(
    private val apiService: ITunesApiService,
    private val rssFeedParser: RssFeedParser,
    private val localStorageManager: LocalStorageManager,
    private val cacheManager: PodcastCacheManager
) : PodcastRepository {

    override suspend fun searchPodcasts(query: String): Result<List<Podcast>> {
        return try {
            // Check cache first
            val cachedResult = cacheManager.getSearchResults(query)
            if (cachedResult != null) {
                return Result.success(cachedResult)
            }
            
            // Fetch from API if not in cache
            val response = apiService.searchPodcasts(query)
            val podcasts = response.results.map { it.toDomainModel() }
            
            // Cache the results
            cacheManager.putSearchResults(query, podcasts)
            
            Result.success(podcasts)
        } catch (e: Exception) {
            // Try to return cached results even if they're expired in case of network error
            val cachedResult = cacheManager.getSearchResults(query)
            if (cachedResult != null) {
                Result.success(cachedResult)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getPodcastDetails(podcastId: String): Result<PodcastDetails> {
        return try {
            // Check cache first
            val cachedDetails = cacheManager.getPodcastDetails(podcastId)
            if (cachedDetails != null) {
                return Result.success(cachedDetails)
            }
            
            val response = apiService.lookupPodcast(podcastId)
            val podcast = response.results.firstOrNull()?.toDomainModel()
                ?: return Result.failure(Exception("Podcast not found"))
            
            val episodes = getEpisodes(podcastId).getOrElse { emptyList() }
            val isSubscribed = localStorageManager.isSubscribed(podcastId)
            val details = PodcastDetails(
                podcast = podcast,
                episodes = episodes,
                isSubscribed = isSubscribed
            )
            
            // Cache the details
            cacheManager.putPodcastDetails(podcastId, details)
            
            Result.success(details)
        } catch (e: Exception) {
            // Try to return cached details even if expired in case of network error
            val cachedDetails = cacheManager.getPodcastDetails(podcastId)
            if (cachedDetails != null) {
                Result.success(cachedDetails)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getEpisodes(podcastId: String): Result<List<PodcastEpisode>> {
        return try {
            // Check cache first
            val cachedEpisodes = cacheManager.getEpisodes(podcastId)
            if (cachedEpisodes != null) {
                return Result.success(cachedEpisodes)
            }
            
            val podcastResponse = apiService.lookupPodcast(podcastId)
            val podcast = podcastResponse.results.firstOrNull()
                ?: return Result.failure(Exception("Podcast not found"))
            
            val feedUrl = podcast.feedUrl
                ?: return Result.failure(Exception("No RSS feed URL available"))
            
            val episodes = rssFeedParser.parseEpisodes(feedUrl, podcastId, podcast.trackName)
            
            // Cache the episodes if successful
            episodes.onSuccess { episodeList ->
                cacheManager.putEpisodes(podcastId, episodeList)
            }
            
            episodes
        } catch (e: Exception) {
            // Try to return cached episodes even if expired in case of network error
            val cachedEpisodes = cacheManager.getEpisodes(podcastId)
            if (cachedEpisodes != null) {
                Result.success(cachedEpisodes)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun subscribeToPodcast(podcast: Podcast): Result<Unit> {
        return try {
            localStorageManager.subscribeToPodcast(podcast)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unsubscribeFromPodcast(podcastId: String): Result<Unit> {
        return try {
            localStorageManager.unsubscribeFromPodcast(podcastId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getSubscribedPodcasts(): Flow<List<Podcast>> {
        return localStorageManager.getSubscribedPodcastsFlow()
    }

    override suspend fun getFeaturedPodcasts(): Result<List<Podcast>> {
        return try {
            // Check cache first
            val cachedFeatured = cacheManager.getFeaturedPodcasts()
            if (cachedFeatured != null) {
                return Result.success(cachedFeatured)
            }
            
            // Get featured podcasts by searching popular categories
            val response = apiService.searchPodcasts("featured podcast", 20)
            val podcasts = response.results.map { it.toDomainModel() }
            
            // Cache the results
            cacheManager.putFeaturedPodcasts(podcasts)
            
            Result.success(podcasts)
        } catch (e: Exception) {
            // Try to return cached results even if expired in case of network error
            val cachedFeatured = cacheManager.getFeaturedPodcasts()
            if (cachedFeatured != null) {
                Result.success(cachedFeatured)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getCategoriesPodcasts(category: String): Result<List<Podcast>> {
        return try {
            // Check cache first
            val cachedCategory = cacheManager.getCategoryPodcasts(category)
            if (cachedCategory != null) {
                return Result.success(cachedCategory)
            }
            
            val response = apiService.searchPodcasts(category, 15)
            val podcasts = response.results
                .filter { it.primaryGenreName?.contains(category, ignoreCase = true) == true }
                .map { it.toDomainModel() }
            
            // Cache the results
            cacheManager.putCategoryPodcasts(category, podcasts)
            
            Result.success(podcasts)
        } catch (e: Exception) {
            // Try to return cached results even if expired in case of network error
            val cachedCategory = cacheManager.getCategoryPodcasts(category)
            if (cachedCategory != null) {
                Result.success(cachedCategory)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getAvailableCategories(): List<String> {
        // iTunes podcast main categories based on Apple Podcasts documentation
        return listOf(
            "Arts",
            "Business", 
            "Comedy",
            "Education",
            "Fiction",
            "Government",
            "Health & Fitness",
            "History",
            "Kids & Family",
            "Leisure",
            "Music",
            "News",
            "Religion & Spirituality",
            "Science",
            "Society & Culture",
            "Sports",
            "Technology",
            "True Crime",
            "TV & Film"
        )
    }

    private fun ITunesPodcast.toDomainModel(): Podcast {
        return Podcast(
            id = trackId.toString(),
            title = trackName,
            description = collectionName ?: "",
            imageUrl = artworkUrl600 ?: artworkUrl100 ?: "",
            author = artistName,
            category = primaryGenreName ?: "",
            language = languageCodesISO2A?.firstOrNull() ?: "en",
            isExplicit = contentAdvisoryRating == "Explicit",
            episodeCount = trackCount ?: 0,
            lastUpdated = System.currentTimeMillis()
        )
    }
}