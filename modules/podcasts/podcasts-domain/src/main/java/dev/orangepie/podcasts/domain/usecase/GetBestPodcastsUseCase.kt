package dev.orangepie.podcasts.domain.usecase

import dev.orangepie.podcasts.data.repository.PodcastsRepository
import dev.orangepie.podcasts.domain.mapper.PodcastChannelMapper
import dev.orangepie.podcasts.domain.model.PodcastChannelModel
import javax.inject.Inject

class GetBestPodcastsUseCase @Inject constructor(
    private val repository: PodcastsRepository,
    private val channelMapper: PodcastChannelMapper
) {

    suspend operator fun invoke(): List<PodcastChannelModel> {
        val repoModel = repository.getBestPodcasts(mapOf(
            "region" to "us",
        ))
        return repoModel.map { channelMapper.toModel(it) }
    }
}