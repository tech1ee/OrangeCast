package dev.orangepie.details.data.repository

import dev.orangepie.details.data.model.PodcastRSSFeedRepoModel

interface PodcastRSSFeedRepository {

    suspend fun getPodcastRSSFeed(url: String): PodcastRSSFeedRepoModel

}