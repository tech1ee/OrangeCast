package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.repository.PodcastRepository

class UnsubscribeFromPodcastUseCase(
    private val repository: PodcastRepository
) {
    suspend operator fun invoke(podcastId: String): Result<Unit> {
        return repository.unsubscribeFromPodcast(podcastId)
    }
}