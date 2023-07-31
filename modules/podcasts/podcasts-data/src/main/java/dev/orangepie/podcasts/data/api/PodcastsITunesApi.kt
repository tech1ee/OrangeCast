package dev.orangepie.podcasts.data.api

import dev.orangepie.podcasts.data.model.PodcastsITunesSearchResult
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface PodcastsITunesApi {

    @GET("/search?parameterkeyvalue")
    suspend fun search(@QueryMap queryMap: Map<String, String>): PodcastsITunesSearchResult
}