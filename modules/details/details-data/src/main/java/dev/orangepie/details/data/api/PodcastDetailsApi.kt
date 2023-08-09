package dev.orangepie.details.data.api

import dev.orangepie.details.data.model.PodcastDetailsITunesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PodcastDetailsApi {

    @GET("/lookup")
    suspend fun lookup(@Query("id") id: Long): PodcastDetailsITunesResponse
}