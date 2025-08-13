package dev.orangecast.shared.data.repository

import dev.orangecast.shared.data.api.ITunesApiService
import dev.orangecast.shared.data.api.model.ITunesPodcast
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
    private val localStorageManager: LocalStorageManager
) : PodcastRepository {

    override suspend fun searchPodcasts(query: String): Result<List<Podcast>> {
        return try {
            val response = apiService.searchPodcasts(query)
            val podcasts = response.results.map { it.toDomainModel() }
            Result.success(podcasts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPodcastDetails(podcastId: String): Result<PodcastDetails> {
        return try {
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
            Result.success(details)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEpisodes(podcastId: String): Result<List<PodcastEpisode>> {
        return try {
            val podcastResponse = apiService.lookupPodcast(podcastId)
            val podcast = podcastResponse.results.firstOrNull()
                ?: return Result.failure(Exception("Podcast not found"))
            
            val feedUrl = podcast.feedUrl
                ?: return Result.failure(Exception("No RSS feed URL available"))
            
            rssFeedParser.parseEpisodes(feedUrl, podcastId, podcast.trackName)
        } catch (e: Exception) {
            Result.failure(e)
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
        // Get featured podcasts by searching popular categories
        return searchPodcasts("featured")
    }

    override suspend fun getCategoriesPodcasts(category: String): Result<List<Podcast>> {
        return searchPodcasts(category)
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