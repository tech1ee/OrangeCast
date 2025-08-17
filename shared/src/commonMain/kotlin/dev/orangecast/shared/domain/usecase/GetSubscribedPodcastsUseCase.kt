package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.domain.repository.PodcastRepository
import kotlinx.coroutines.flow.Flow

class GetSubscribedPodcastsUseCase(
    private val repository: PodcastRepository
) {
    
    fun getSubscribedPodcasts(): Flow<List<Podcast>> {
        return repository.getSubscribedPodcasts()
    }
}