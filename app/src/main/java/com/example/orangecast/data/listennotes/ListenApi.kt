package com.example.orangecast.data.listennotes

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ListenApi {

    @GET("/best_podcasts")
    suspend fun getBestPodcasts(@QueryMap queryMap: Map<String, String>): Response<BestPodcastsListen>
}