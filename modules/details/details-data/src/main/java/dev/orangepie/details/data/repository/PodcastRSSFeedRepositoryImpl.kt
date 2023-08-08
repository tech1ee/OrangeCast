package dev.orangepie.details.data.repository

import dev.orangepie.details.data.api.PodcastRSSFeedApi
import dev.orangepie.details.data.mapper.PodcastRSSFeedRepoMapper
import dev.orangepie.details.data.model.PodcastRSSFeedRepoModel
import javax.inject.Inject

class PodcastRSSFeedRepositoryImpl @Inject constructor(
    private val api: PodcastRSSFeedApi,
    private val mapper: PodcastRSSFeedRepoMapper,
): PodcastRSSFeedRepository {

    override suspend fun getPodcastRSSFeed(url: String): PodcastRSSFeedRepoModel {
        val response = api.getPodcastRSSFeed(url)
        return mapper.toRepoModel(response)
    }
}