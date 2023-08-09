package dev.orangepie.details.data.repository

import dev.orangepie.details.data.api.PodcastDetailsApi
import dev.orangepie.details.data.mapper.PodcastDetailsRepoMapper
import dev.orangepie.details.data.model.PodcastDetailsRepoModel
import javax.inject.Inject

class PodcastDetailsRepositoryImpl @Inject constructor(
    private val api: PodcastDetailsApi,
    private val mapper: PodcastDetailsRepoMapper,
): PodcastDetailsRepository {

    override suspend fun getPodcastDetails(id: Long): PodcastDetailsRepoModel {
        return api.lookup(id).let(mapper::toRepoModel)
    }
}