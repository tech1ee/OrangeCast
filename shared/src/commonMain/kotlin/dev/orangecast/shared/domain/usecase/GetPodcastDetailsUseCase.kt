package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.PodcastDetails
import dev.orangecast.shared.domain.repository.PodcastRepository

class GetPodcastDetailsUseCase(
    private val podcastRepository: PodcastRepository
) {
    suspend operator fun invoke(podcastId: String): Result<PodcastDetails> {
        return podcastRepository.getPodcastDetails(podcastId)
    }
}