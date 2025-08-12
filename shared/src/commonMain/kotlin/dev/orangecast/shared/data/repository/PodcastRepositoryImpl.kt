package dev.orangecast.shared.data.repository

import dev.orangecast.shared.data.api.ITunesApiService
import dev.orangecast.shared.data.api.model.ITunesPodcast
import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.domain.model.PodcastDetails
import dev.orangecast.shared.domain.model.PodcastEpisode
import dev.orangecast.shared.domain.repository.PodcastRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class PodcastRepositoryImpl(
    private val apiService: ITunesApiService
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
            val details = PodcastDetails(
                podcast = podcast,
                episodes = episodes,
                isSubscribed = false
            )
            Result.success(details)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEpisodes(podcastId: String): Result<List<PodcastEpisode>> {
        return try {
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun subscribeToPodcast(podcast: Podcast): Result<Unit> {
        return try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unsubscribeFromPodcast(podcastId: String): Result<Unit> {
        return try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getSubscribedPodcasts(): Flow<List<Podcast>> {
        return flowOf(emptyList())
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