package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.domain.repository.PodcastRepository

class SearchPodcastsUseCase(
    private val podcastRepository: PodcastRepository
) {
    suspend operator fun invoke(query: String): Result<List<Podcast>> {
        if (query.isBlank()) {
            return Result.success(emptyList())
        }
        return podcastRepository.searchPodcasts(query.trim())
    }
}