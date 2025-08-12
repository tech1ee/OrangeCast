package dev.orangecast.shared.domain.repository

import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.domain.model.PodcastDetails
import dev.orangecast.shared.domain.model.PodcastEpisode
import kotlinx.coroutines.flow.Flow

interface PodcastRepository {
    suspend fun searchPodcasts(query: String): Result<List<Podcast>>
    suspend fun getPodcastDetails(podcastId: String): Result<PodcastDetails>
    suspend fun getEpisodes(podcastId: String): Result<List<PodcastEpisode>>
    suspend fun subscribeToPodcast(podcast: Podcast): Result<Unit>
    suspend fun unsubscribeFromPodcast(podcastId: String): Result<Unit>
    fun getSubscribedPodcasts(): Flow<List<Podcast>>
    suspend fun getFeaturedPodcasts(): Result<List<Podcast>>
    suspend fun getCategoriesPodcasts(category: String): Result<List<Podcast>>
}