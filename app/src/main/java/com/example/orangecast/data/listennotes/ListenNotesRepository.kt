package com.example.orangecast.data.listennotes

import com.example.orangecast.data.listennotes.entity.BestPodcastsListen
import com.example.orangecast.data.listennotes.entity.ChannelListen

interface ListenNotesRepository {

    suspend fun getBestPodcasts(
            queryMap: Map<String, String>
    ): BestPodcastsListen

    suspend fun getPodcastDetails(
        queryMap: Map<String, String>
    ): ChannelListen
}