package com.example.orangecast.data.listennotes

import com.example.orangecast.data.listennotes.entity.BestPodcastsListen
import com.example.orangecast.data.listennotes.entity.ChannelListen

interface ListenApi {

    @GET("best_podcasts")
    suspend fun getBestPodcasts(@QueryMap queryMap: Map<String, String>): Response<BestPodcastsListen>

    @GET("podcasts")
    suspend fun getPodcastDetails(@QueryMap queryMap: Map<String, String>): Response<ChannelListen>
}