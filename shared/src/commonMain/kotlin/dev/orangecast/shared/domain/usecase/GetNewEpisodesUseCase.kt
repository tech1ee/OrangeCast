package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.data.database.DatabaseRepository
import dev.orangecast.shared.domain.model.PodcastEpisode
import dev.orangecast.shared.domain.repository.PodcastRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNewEpisodesUseCase(
    private val repository: PodcastRepository,
    private val databaseRepository: DatabaseRepository
) {
    
    fun getNewEpisodes(limit: Int = 50): Flow<List<PodcastEpisode>> {
        return databaseRepository.getNewEpisodesFromSubscribedPodcasts(limit.toLong()).map { episodes ->
            episodes.map { episode ->
                PodcastEpisode(
                    id = episode.id,
                    title = episode.title,
                    description = episode.description ?: "",
                    audioUrl = episode.audioUrl,
                    duration = episode.duration ?: 0L,
                    publishedAt = episode.publishedAt,
                    thumbnailUrl = episode.podcastImageUrl ?: "",
                    podcastId = episode.podcastId,
                    podcastTitle = episode.podcastTitle ?: ""
                )
            }
        }
    }
    
    fun getUnplayedEpisodes(): Flow<List<PodcastEpisode>> {
        return databaseRepository.getUnplayedEpisodes().map { entities ->
            entities.map { entity ->
                PodcastEpisode(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description ?: "",
                    audioUrl = entity.audioUrl,
                    duration = entity.duration ?: 0L,
                    publishedAt = entity.publishedAt,
                    thumbnailUrl = "",
                    podcastId = entity.podcastId,
                    podcastTitle = ""
                )
            }
        }
    }
}