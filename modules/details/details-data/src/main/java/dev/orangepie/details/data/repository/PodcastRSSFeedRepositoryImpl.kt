package dev.orangepie.details.data.repository

import dev.orangepie.details.data.mapper.PodcastRSSFeedRepoMapper
import dev.orangepie.details.data.model.PodcastRSSFeedRepoModel
import dev.orangepie.details.data.rss.RSSParser
import javax.inject.Inject

class PodcastRSSFeedRepositoryImpl @Inject constructor(
    private val parser: RSSParser,
    private val mapper: PodcastRSSFeedRepoMapper,
): PodcastRSSFeedRepository {

    override suspend fun getPodcastRSSFeed(url: String): PodcastRSSFeedRepoModel {
        val response = parser.parse(url)
        return mapper.toRepoModel(response)
    }
}