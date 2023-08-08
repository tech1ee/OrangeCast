package dev.orangepie.details.domain.usecase

import dev.orangepie.details.data.repository.PodcastDetailsRepository
import dev.orangepie.details.domain.mapper.PodcastDetailsMapper
import javax.inject.Inject

class GetPodcastDetailsUseCase @Inject constructor(
    private val repository: PodcastDetailsRepository,
    private val mapper: PodcastDetailsMapper,
) {

    suspend operator fun invoke(id: Long) = mapper.toModel(repository.getPodcastDetails(id))
}