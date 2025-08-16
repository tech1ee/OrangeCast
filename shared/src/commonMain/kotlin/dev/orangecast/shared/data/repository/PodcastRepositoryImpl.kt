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
            // Check cache first - but don't return empty episode results immediately  
            val cachedDetails = cacheManager.getPodcastDetails(podcastId)
            if (cachedDetails != null && cachedDetails.episodes.isNotEmpty()) {
                return Result.success(cachedDetails)
            }
            
            val response = apiService.lookupPodcast(podcastId)
            val podcast = response.results.firstOrNull()?.toDomainModel()
                ?: return Result.failure(Exception("Podcast not found"))
            
            // Get episodes with better error reporting
            val episodesResult = getEpisodes(podcastId)
            val episodes = when {
                episodesResult.isSuccess -> {
                    episodesResult.getOrNull() ?: emptyList()
                }
                else -> {
                    emptyList() // Still create podcast details even without episodes
                }
            }
            
            val isSubscribed = localStorageManager.isSubscribed(podcastId)
            
            val details = PodcastDetails(
                podcast = podcast,
                episodes = episodes,
                isSubscribed = isSubscribed
            )
            
            // Only cache details if we have episodes
            if (episodes.isNotEmpty()) {
                cacheManager.putPodcastDetails(podcastId, details)
            }
            
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
            // Check cache first - but don't return empty results immediately
            val cachedEpisodes = cacheManager.getEpisodes(podcastId)
            if (cachedEpisodes != null && cachedEpisodes.isNotEmpty()) {
                return Result.success(cachedEpisodes)
            }
            
            // Clear any empty cached results to force refresh
            if (cachedEpisodes != null && cachedEpisodes.isEmpty()) {
                cacheManager.removeEpisodes(podcastId)
            }
            
            val podcastResponse = apiService.lookupPodcast(podcastId)
            val podcast = podcastResponse.results.firstOrNull()
                ?: return Result.failure(Exception("Podcast not found"))
            
            // First try the feedUrl from iTunes API if available
            var feedUrl = podcast.feedUrl
            var episodes: Result<List<PodcastEpisode>> = Result.failure(Exception("No feed URL"))
            
            if (feedUrl != null) {
                episodes = rssFeedParser.parseEpisodes(feedUrl, podcastId, podcast.trackName)
            }
            
            // If iTunes feedUrl failed or wasn't available, try alternative sources
            if (episodes.isFailure) {
                val alternativeFeedUrl = tryAlternativeFeedSources(podcast.trackName, podcast.artistName)
                if (alternativeFeedUrl != null) {
                    feedUrl = alternativeFeedUrl
                    episodes = rssFeedParser.parseEpisodes(feedUrl, podcastId, podcast.trackName)
                } else {
                    return Result.failure(Exception("No RSS feed URL available for ${podcast.trackName}"))
                }
            }
            
            episodes.fold(
                onSuccess = { episodeList ->
                    // Only cache non-empty results
                    if (episodeList.isNotEmpty()) {
                        cacheManager.putEpisodes(podcastId, episodeList)
                    }
                    Result.success(episodeList)
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
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

    private suspend fun tryAlternativeFeedSources(podcastName: String, artistName: String?): String? {
        return try {
            // First try known popular podcasts with reliable RSS feeds
            val knownFeed = getKnownPodcastFeed(podcastName)
            if (knownFeed != null) return knownFeed
            
            // Try searching for the podcast again with different parameters to get feedUrl
            val searchQuery = if (!artistName.isNullOrBlank()) {
                "$podcastName $artistName"
            } else {
                podcastName
            }
            
            val searchResponse = apiService.searchPodcasts(searchQuery, limit = 10)
            searchResponse.results
                .firstOrNull { it.feedUrl != null && it.trackName.contains(podcastName, ignoreCase = true) }
                ?.feedUrl
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getKnownPodcastFeed(podcastName: String): String? {
        // Popular podcasts with known working RSS feeds
        return when {
            podcastName.contains("Serial", ignoreCase = true) -> 
                "https://feeds.serialpodcast.org/serialpodcast"
            podcastName.contains("This American Life", ignoreCase = true) -> 
                "https://feeds.thisamericanlife.org/talpodcast"
            podcastName.contains("The Daily", ignoreCase = true) -> 
                "https://feeds.nytimes.com/nyt/rss/podcasts/the-daily"
            podcastName.contains("Conan", ignoreCase = true) -> 
                "https://feeds.simplecast.com/dHoohVNH"
            podcastName.contains("RadioLab", ignoreCase = true) -> 
                "https://feeds.wnyc.org/radiolab"
            podcastName.contains("Planet Money", ignoreCase = true) -> 
                "https://feeds.npr.org/510289/podcast.xml"
            podcastName.contains("Fresh Air", ignoreCase = true) -> 
                "https://feeds.npr.org/381444908/podcast.xml"
            podcastName.contains("TED Radio Hour", ignoreCase = true) -> 
                "https://feeds.npr.org/510298/podcast.xml"
            podcastName.contains("How I Built This", ignoreCase = true) -> 
                "https://feeds.npr.org/510313/podcast.xml"
            else -> null
        }
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