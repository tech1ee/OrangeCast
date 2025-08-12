package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.PodcastEpisode
import dev.orangecast.shared.domain.repository.PodcastRepository
import kotlinx.coroutines.flow.Flow

class GetNewEpisodesUseCase(
    private val podcastRepository: PodcastRepository
) {
    operator fun invoke(limit: Int = 50): Flow<List<PodcastEpisode>> {
        return podcastRepository.getNewEpisodesFromSubscriptions(limit)
    }
}