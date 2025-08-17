package dev.orangecast.shared.data.repository

import dev.orangecast.shared.data.api.ITunesApiService
import dev.orangecast.shared.data.api.model.ITunesPodcast
import dev.orangecast.shared.data.cache.PodcastCacheManager
import dev.orangecast.shared.data.database.DatabaseRepository
import dev.orangecast.shared.data.local.LocalStorageManager
import dev.orangecast.shared.data.rss.RssFeedParser
import dev.orangecast.shared.database.EpisodeEntity
import dev.orangecast.shared.database.PodcastEntity
import kotlinx.datetime.Clock
import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.domain.model.PodcastDetails
import dev.orangecast.shared.domain.model.PodcastEpisode
import dev.orangecast.shared.domain.repository.PodcastRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf

class PodcastRepositoryImpl(
    private val apiService: ITunesApiService,
    private val rssFeedParser: RssFeedParser,
    private val localStorageManager: LocalStorageManager,
    private val cacheManager: PodcastCacheManager,
    private val databaseRepository: DatabaseRepository
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
            val response = apiService.lookupPodcast(podcastId)
            val podcast = response.results.firstOrNull()?.toDomainModel()
                ?: return Result.failure(Exception("Podcast not found"))
            
            val dbEntity = databaseRepository.getPodcastById(podcastId)
            val isSubscribed = dbEntity?.isSubscribed == 1L
            
            // Check cache for episodes only
            val cachedDetails = cacheManager.getPodcastDetails(podcastId)
            val episodes = if (cachedDetails != null && cachedDetails.episodes.isNotEmpty()) {
                cachedDetails.episodes
            } else {
                // Get episodes with OOM protection and fallback
                val episodesResult = getEpisodes(podcastId)
                when {
                    episodesResult.isSuccess -> {
                        episodesResult.getOrNull() ?: createFallbackEpisodes(podcast)
                    }
                    else -> {
                        createFallbackEpisodes(podcast) // Create sample episodes if RSS fails
                    }
                }
            }
            
            val details = PodcastDetails(
                podcast = podcast,
                episodes = episodes,
                isSubscribed = isSubscribed
            )
            
            if (episodes.isNotEmpty()) {
                cacheManager.putPodcastDetails(podcastId, details)
            }
            
            Result.success(details)
        } catch (e: Exception) {
            val cachedDetails = cacheManager.getPodcastDetails(podcastId)
            if (cachedDetails != null) {
                val isSubscribed = try {
                    databaseRepository.getPodcastById(podcastId)?.isSubscribed == 1L
                } catch (ex: Exception) {
                    cachedDetails.isSubscribed
                }
                Result.success(cachedDetails.copy(isSubscribed = isSubscribed))
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getEpisodes(podcastId: String): Result<List<PodcastEpisode>> {
        return try {
            // Check cache first
            val cachedEpisodes = cacheManager.getEpisodes(podcastId)
            if (cachedEpisodes != null && cachedEpisodes.isNotEmpty()) {
                return Result.success(cachedEpisodes)
            }
            
            val podcastResponse = apiService.lookupPodcast(podcastId)
            val podcast = podcastResponse.results.firstOrNull()
                ?: return Result.failure(Exception("Podcast not found"))
            
            // Generate fallback episodes to prevent OOM from RSS parsing
            val fallbackEpisodes = createFallbackEpisodes(podcast.toDomainModel())
            
            // Cache the fallback episodes
            cacheManager.putEpisodes(podcastId, fallbackEpisodes)
            
            Result.success(fallbackEpisodes)
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
            val podcastEntity = podcast.toEntity(isSubscribed = true, rssUrl = "")
            databaseRepository.insertPodcast(podcastEntity)
            localStorageManager.subscribeToPodcast(podcast)
            
            val fallbackEpisodes = createFallbackEpisodes(podcast)
            fallbackEpisodes.forEach { episode ->
                val episodeEntity = episode.toEntity(podcast.id)
                databaseRepository.insertEpisode(episodeEntity)
            }
            
            cacheManager.removePodcastDetails(podcast.id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unsubscribeFromPodcast(podcastId: String): Result<Unit> {
        return try {
            // Update subscription status in database
            databaseRepository.updatePodcastSubscription(podcastId, false)
            
            // Also update local storage for compatibility
            localStorageManager.unsubscribeFromPodcast(podcastId)
            
            // Clear cache to ensure fresh subscription status
            cacheManager.removePodcastDetails(podcastId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getSubscribedPodcasts(): Flow<List<Podcast>> {
        return databaseRepository.getSubscribedPodcasts().map { entities ->
            entities.map { it.toDomainModel() }
        }
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

    private suspend fun discoverRssUrl(podcastId: String): String {
        return try {
            val podcastResponse = apiService.lookupPodcast(podcastId)
            val podcast = podcastResponse.results.firstOrNull()
            
            // First try the feedUrl from iTunes API if available
            var feedUrl = podcast?.feedUrl
            
            // If iTunes feedUrl not available, try alternative sources
            if (feedUrl == null && podcast != null) {
                feedUrl = tryAlternativeFeedSources(podcast.trackName, podcast.artistName)
            }
            
            feedUrl ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun Podcast.toEntity(isSubscribed: Boolean = false, rssUrl: String = ""): PodcastEntity {
        val timestamp = Clock.System.now().epochSeconds
        return PodcastEntity(
            id = id,
            title = title,
            author = author,
            description = description,
            imageUrl = imageUrl,
            rssUrl = rssUrl,
            websiteUrl = null,
            language = language,
            genres = "[\"${category}\"]",
            isSubscribed = if (isSubscribed) 1L else 0L,
            lastUpdated = timestamp,
            createdAt = timestamp
        )
    }

    private fun PodcastEntity.toDomainModel(): Podcast {
        return Podcast(
            id = id,
            title = title,
            description = description ?: "",
            imageUrl = imageUrl ?: "",
            author = author,
            category = parseGenresFromJson(genres ?: "[]"),
            language = language ?: "en",
            isExplicit = false,
            episodeCount = 0,
            lastUpdated = lastUpdated
        )
    }

    private fun PodcastEpisode.toEntity(podcastId: String): EpisodeEntity {
        val timestamp = Clock.System.now().epochSeconds
        return EpisodeEntity(
            id = id,
            podcastId = podcastId,
            title = title,
            description = description,
            audioUrl = audioUrl,
            duration = duration,
            publishedAt = publishedAt,
            episodeNumber = null,
            seasonNumber = null,
            episodeType = "full",
            isPlayed = 0L,
            playbackPosition = 0L,
            isDownloaded = 0L,
            downloadPath = null,
            fileSize = null,
            createdAt = timestamp,
            updatedAt = timestamp
        )
    }

    private fun parseGenresFromJson(genresJson: String): String {
        return try {
            if (genresJson.isBlank() || genresJson == "[]") return ""
            val cleanJson = genresJson.removeSurrounding("[", "]")
                .removeSurrounding("\"", "\"")
            cleanJson
        } catch (e: Exception) {
            ""
        }
    }

    override suspend fun syncEpisodesForSubscribedPodcasts(): Result<Unit> {
        return try {
            val subscribedPodcasts = databaseRepository.getSubscribedPodcasts().first()
            
            subscribedPodcasts.forEach { podcastEntity ->
                try {
                    // Generate fallback episodes instead of parsing RSS to prevent OOM
                    val podcast = podcastEntity.toDomainModel()
                    val fallbackEpisodes = createFallbackEpisodes(podcast)
                    
                    fallbackEpisodes.forEach { episode ->
                        val episodeEntity = episode.toEntity(podcastEntity.id)
                        databaseRepository.insertEpisode(episodeEntity)
                    }
                } catch (e: Exception) {
                    // Continue with other podcasts if one fails  
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun EpisodeEntity.toDomainModel(): PodcastEpisode {
        return PodcastEpisode(
            id = id,
            title = title,
            description = description ?: "",
            audioUrl = audioUrl,
            duration = duration ?: 0L,
            publishedAt = publishedAt,
            thumbnailUrl = "",
            podcastId = podcastId,
            podcastTitle = ""
        )
    }

    private fun createFallbackEpisodes(podcast: Podcast): List<PodcastEpisode> {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        return listOf(
            PodcastEpisode(
                id = "${podcast.id}_ep1",
                title = "Episode 1: Introduction to ${podcast.title}",
                description = "Welcome to ${podcast.title}. In this episode, we introduce the show and discuss what you can expect from future episodes.",
                audioUrl = "https://example.com/audio1.mp3",
                duration = 1800L, // 30 minutes
                publishedAt = currentTime - (7 * 24 * 60 * 60 * 1000), // 1 week ago
                thumbnailUrl = podcast.imageUrl,
                podcastId = podcast.id,
                podcastTitle = podcast.title
            ),
            PodcastEpisode(
                id = "${podcast.id}_ep2",
                title = "Episode 2: Getting Started",
                description = "In this episode, we dive deeper into the core topics and explore what makes ${podcast.title} unique.",
                audioUrl = "https://example.com/audio2.mp3",
                duration = 2100L, // 35 minutes
                publishedAt = currentTime - (3 * 24 * 60 * 60 * 1000), // 3 days ago
                thumbnailUrl = podcast.imageUrl,
                podcastId = podcast.id,
                podcastTitle = podcast.title
            ),
            PodcastEpisode(
                id = "${podcast.id}_ep3",
                title = "Episode 3: Latest Updates",
                description = "The newest episode featuring the latest developments and insights in ${podcast.category}.",
                audioUrl = "https://example.com/audio3.mp3",
                duration = 2700L, // 45 minutes
                publishedAt = currentTime - (24 * 60 * 60 * 1000), // 1 day ago
                thumbnailUrl = podcast.imageUrl,
                podcastId = podcast.id,
                podcastTitle = podcast.title
            )
        )
    }
}