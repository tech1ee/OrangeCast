package dev.orangepie.details.data.api

import dev.orangepie.details.data.model.PodcastRSSFeedResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface PodcastRSSFeedApi {

    @GET
    suspend fun getPodcastRSSFeed(@Url url: String): PodcastRSSFeedResponse
}