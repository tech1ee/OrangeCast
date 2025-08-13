package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.PodcastEpisode
import dev.orangecast.shared.domain.repository.PodcastRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class GetNewEpisodesUseCase(
    private val repository: PodcastRepository
) {
    operator fun invoke(limit: Int = 50): Flow<Result<List<PodcastEpisode>>> = flow {
        try {
            val subscribedPodcasts = repository.getSubscribedPodcasts().first()
            
            if (subscribedPodcasts.isEmpty()) {
                emit(Result.success(emptyList()))
                return@flow
            }
            
            val allEpisodes = mutableListOf<PodcastEpisode>()
            
            subscribedPodcasts.forEach { podcast ->
                repository.getEpisodes(podcast.id)
                    .onSuccess { episodes ->
                        allEpisodes.addAll(episodes)
                    }
            }
            
            val recentEpisodes = allEpisodes
                .sortedByDescending { it.publishedAt }
                .take(limit)
            
            emit(Result.success(recentEpisodes))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}