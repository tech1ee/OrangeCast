package dev.orangepie.podcasts.data.api

import dev.orangepie.podcasts.data.model.PodcastsBestListenNotesResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface PodcastsListenNotesApi {

    @GET("best_podcasts")
    suspend fun getBestPodcasts(@QueryMap queryMap: Map<String, String>): PodcastsBestListenNotesResponse

}