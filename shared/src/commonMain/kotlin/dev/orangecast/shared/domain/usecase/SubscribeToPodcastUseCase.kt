package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.domain.repository.PodcastRepository

class SubscribeToPodcastUseCase(
    private val repository: PodcastRepository
) {
    suspend operator fun invoke(podcast: Podcast): Result<Unit> {
        return repository.subscribeToPodcast(podcast)
    }
}