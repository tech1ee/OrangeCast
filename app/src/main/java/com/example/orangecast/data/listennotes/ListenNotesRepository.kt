package com.example.orangecast.data.listennotes

import com.example.orangecast.data.listennotes.entity.BestPodcastsListen

interface ListenNotesRepository {

    suspend fun getBestPodcasts(
            queryMap: Map<String, String>
    ): BestPodcastsListen
}