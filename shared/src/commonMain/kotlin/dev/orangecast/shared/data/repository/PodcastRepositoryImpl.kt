package dev.orangecast.shared.data.repository

import dev.orangecast.shared.data.api.ListenNotesApiService
import dev.orangecast.shared.data.api.model.ListenNotesPodcast
import dev.orangecast.shared.data.cache.PodcastCacheManager
import dev.orangecast.shared.data.database.DatabaseRepository
import dev.orangecast.shared.data.rss.RssFeedParser
import dev.orangecast.shared.database.EpisodeEntity
import dev.orangecast.shared.database.PodcastEntity
import kotlinx.datetime.Clock
import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.domain.model.PodcastDetails
import dev.orangecast.shared.domain.model.PodcastEpisode
import dev.orangecast.shared.domain.model.Genre
import dev.orangecast.shared.domain.repository.PodcastRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf
import io.github.aakira.napier.Napier

class PodcastRepositoryImpl(
    private val apiService: ListenNotesApiService,
    private val rssFeedParser: RssFeedParser,
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
            Napier.e("Failed to search podcasts for query: $query", e, tag = "PodcastRepository")
            // Try to return cached results even if they're expired in case of network error
            val cachedResult = cacheManager.getSearchResults(query)
            if (cachedResult != null) {
                Napier.i("Returning cached results for query: $query", tag = "PodcastRepository")
                Result.success(cachedResult)
            } else {
                Napier.e("No cached results available for query: $query", tag = "PodcastRepository")
                Result.failure(e)
            }
        }
    }

    override suspend fun getPodcastDetails(podcastId: String): Result<PodcastDetails> {
        return try {
            val podcast = apiService.lookupPodcast(podcastId).toDomainModel()
            
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
                        episodesResult.getOrNull() ?: emptyList()
                    }
                    else -> {
                        emptyList() // Return empty list if RSS fails - no fake episodes
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
            Napier.e("Failed to get podcast details for ID: $podcastId", e, tag = "PodcastRepository")
            val cachedDetails = cacheManager.getPodcastDetails(podcastId)
            if (cachedDetails != null) {
                Napier.i("Returning cached details for podcast ID: $podcastId", tag = "PodcastRepository")
                val isSubscribed = try {
                    databaseRepository.getPodcastById(podcastId)?.isSubscribed == 1L
                } catch (ex: Exception) {
                    Napier.e("Failed to get subscription status for podcast ID: $podcastId", ex, tag = "PodcastRepository")
                    cachedDetails.isSubscribed
                }
                Result.success(cachedDetails.copy(isSubscribed = isSubscribed))
            } else {
                Napier.e("No cached details available for podcast ID: $podcastId", tag = "PodcastRepository")
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
            
            val podcast = apiService.lookupPodcast(podcastId)
            
            // Generate fallback episodes to prevent OOM from RSS parsing
            // Use real RSS parsing instead of fake episodes
            val episodes = if (!podcast.rss.isNullOrBlank()) {
                rssFeedParser.parseEpisodes(podcast.rss, podcast.id, podcast.title).getOrNull() ?: emptyList()
            } else {
                emptyList()
            }
            
            // Cache the real episodes
            cacheManager.putEpisodes(podcastId, episodes)
            
            Result.success(episodes)
        } catch (e: Exception) {
            Napier.e("Failed to get episodes for podcast ID: $podcastId", e, tag = "PodcastRepository")
            // Try to return cached episodes even if expired in case of network error
            val cachedEpisodes = cacheManager.getEpisodes(podcastId)
            if (cachedEpisodes != null) {
                Napier.i("Returning cached episodes for podcast ID: $podcastId", tag = "PodcastRepository")
                Result.success(cachedEpisodes)
            } else {
                Napier.e("No cached episodes available for podcast ID: $podcastId", tag = "PodcastRepository")
                Result.failure(e)
            }
        }
    }

    override suspend fun subscribeToPodcast(podcast: Podcast): Result<Unit> {
        return try {
            // First get full podcast details from API to get RSS URL
            val fullPodcast = apiService.lookupPodcast(podcast.id)
            val podcastEntity = podcast.toEntity(isSubscribed = true, rssUrl = fullPodcast.rss ?: "")
            databaseRepository.insertPodcast(podcastEntity).getOrThrow()
            
            // Use real RSS parsing instead of fake episodes  
            val episodes = if (!fullPodcast.rss.isNullOrBlank()) {
                rssFeedParser.parseEpisodes(fullPodcast.rss, podcast.id, podcast.title).getOrNull() ?: emptyList()
            } else {
                emptyList() 
            }
            episodes.forEach { episode ->
                val episodeEntity = episode.toEntity(podcast.id)
                databaseRepository.insertEpisode(episodeEntity).getOrThrow()
            }
            
            cacheManager.removePodcastDetails(podcast.id)
            Result.success(Unit)
        } catch (e: Exception) {
            Napier.e("Failed to subscribe to podcast: ${podcast.title}", e, tag = "PodcastRepository")
            Result.failure(e)
        }
    }

    override suspend fun unsubscribeFromPodcast(podcastId: String): Result<Unit> {
        return try {
            // Update subscription status in database
            databaseRepository.updatePodcastSubscription(podcastId, false).getOrThrow()
            
            // Clear cache to ensure fresh subscription status
            cacheManager.removePodcastDetails(podcastId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Napier.e("Failed to unsubscribe from podcast ID: $podcastId", e, tag = "PodcastRepository")
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
            
            // Get featured podcasts from ListenNotes best podcasts
            val response = apiService.getBestPodcasts()
            val podcasts = response.podcasts.map { it.toDomainModel() }
            
            // Cache the results
            cacheManager.putFeaturedPodcasts(podcasts)
            
            Result.success(podcasts)
        } catch (e: Exception) {
            Napier.e("Failed to get featured podcasts", e, tag = "PodcastRepository")
            // Try to return cached results even if expired in case of network error
            val cachedFeatured = cacheManager.getFeaturedPodcasts()
            if (cachedFeatured != null) {
                Napier.i("Returning cached featured podcasts", tag = "PodcastRepository")
                Result.success(cachedFeatured)
            } else {
                Napier.e("No cached featured podcasts available", tag = "PodcastRepository")
                Result.failure(e)
            }
        }
    }

    override suspend fun getGenres(): Result<List<Genre>> {
        return try {
            val response = apiService.getGenres()
            val genres = response.genres.map { genreDto ->
                Genre(
                    id = genreDto.id,
                    name = genreDto.name,
                    parentId = genreDto.parentId
                )
            }
            Napier.d("Loaded ${genres.size} genres from API", tag = "PodcastRepository")
            Result.success(genres)
        } catch (e: Exception) {
            Napier.e("Failed to load genres from API", e, tag = "PodcastRepository")
            Result.failure(e)
        }
    }

    override suspend fun getPodcastsByGenre(genreId: Int): Result<List<Podcast>> {
        return try {
            // Check cache first 
            val cachedGenre = cacheManager.getCategoryPodcasts(genreId.toString())
            if (cachedGenre != null) {
                return Result.success(cachedGenre)
            }
            
            val response = apiService.getBestPodcasts(genreId = genreId)
            val podcasts = response.podcasts.map { it.toDomainModel() }
            
            // Cache the results
            cacheManager.putCategoryPodcasts(genreId.toString(), podcasts)
            
            Napier.d("Found ${podcasts.size} podcasts for genre ID: $genreId", tag = "PodcastRepository")
            Result.success(podcasts)
        } catch (e: Exception) {
            Napier.e("Failed to get podcasts for genre ID: $genreId", e, tag = "PodcastRepository")
            // Try to return cached results even if expired
            val cachedGenre = cacheManager.getCategoryPodcasts(genreId.toString())
            if (cachedGenre != null) {
                Napier.i("Returning cached podcasts for genre ID: $genreId", tag = "PodcastRepository")
                Result.success(cachedGenre)
            } else {
                Napier.e("No cached podcasts available for genre ID: $genreId", tag = "PodcastRepository")
                Result.failure(e)
            }
        }
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
            
            val searchResponse = apiService.searchPodcasts(searchQuery, 10)
            searchResponse.results
                .firstOrNull { it.rss != null && it.title.contains(podcastName, ignoreCase = true) }
                ?.rss
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
    

    private fun ListenNotesPodcast.toDomainModel(): Podcast {
        return Podcast(
            id = id,
            title = title,
            description = description,
            imageUrl = image,
            author = publisher,
            category = genreIds.firstOrNull()?.toString() ?: "",
            language = language ?: "en",
            isExplicit = explicitContent,
            episodeCount = totalEpisodes,
            lastUpdated = System.currentTimeMillis()
        )
    }

    private suspend fun discoverRssUrl(podcastId: String): String {
        return try {
            val podcast = apiService.lookupPodcast(podcastId)
            
            // First try the RSS URL from ListenNotes API if available
            var feedUrl = podcast.rss
            
            // If ListenNotes RSS not available, try alternative sources
            if (feedUrl == null) {
                feedUrl = tryAlternativeFeedSources(podcast.title, podcast.publisher)
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
            listenNotesId = id, // Use the same ID as ListenNotes ID
            genreIds = "[\"${category}\"]", // Store category as genre IDs
            episodeCount = episodeCount.toLong(),
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
                    // Use real RSS parsing instead of fake episodes
                    val podcast = podcastEntity.toDomainModel()
                    val episodes = if (podcastEntity.rssUrl.isNotBlank()) {
                        rssFeedParser.parseEpisodes(podcastEntity.rssUrl, podcastEntity.id, podcastEntity.title ?: "").getOrNull() ?: emptyList()
                    } else {
                        emptyList()
                    }
                    
                    episodes.forEach { episode ->
                        val episodeEntity = episode.toEntity(podcastEntity.id)
                        databaseRepository.insertEpisode(episodeEntity).getOrThrow()
                    }
                } catch (e: Exception) {
                    // Continue with other podcasts if one fails  
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Napier.e("Failed to sync episodes for subscribed podcasts", e, tag = "PodcastRepository")
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

}