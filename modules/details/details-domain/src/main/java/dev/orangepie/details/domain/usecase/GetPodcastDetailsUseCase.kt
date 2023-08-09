package dev.orangepie.details.domain.usecase

import dev.orangepie.details.data.repository.PodcastDetailsRepository
import dev.orangepie.details.data.repository.PodcastRSSFeedRepository
import dev.orangepie.details.domain.mapper.PodcastDetailsMapper
import dev.orangepie.details.domain.model.PodcastDetailsModel
import javax.inject.Inject

class GetPodcastDetailsUseCase @Inject constructor(
    private val detailsRepository: PodcastDetailsRepository,
    private val detailsMapper: PodcastDetailsMapper,
    private val feedRepository: PodcastRSSFeedRepository,
) {

    suspend operator fun invoke(id: Long): PodcastDetailsModel {
        val detailsRepoModel = detailsRepository.getPodcastDetails(id)
        val feedRepoModel = feedRepository.getPodcastRSSFeed(detailsRepoModel.feedUrl)
        return detailsMapper.toModel(
            repoModel = detailsRepoModel,
            feedRepoModel = feedRepoModel
        )
    }
}