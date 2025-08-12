package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.data.repository.PodcastRepositoryImpl

class CleanupDatabaseUseCase(
    private val podcastRepository: PodcastRepositoryImpl
) {
    suspend operator fun invoke(): Result<Unit> {
        return podcastRepository.cleanupDatabaseDuplicates()
    }
}