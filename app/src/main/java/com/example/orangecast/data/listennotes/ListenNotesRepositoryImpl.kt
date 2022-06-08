package com.example.orangecast.data.listennotes

import com.example.orangecast.data.listennotes.entity.BestPodcastsListen
import com.example.orangecast.data.listennotes.entity.ChannelListen
import com.example.orangecast.data.listennotes.entity.RequestUnsuccessfulException
import javax.inject.Inject

class ListenNotesRepositoryImpl @Inject constructor(
        private val api: ListenApi
): ListenNotesRepository {

    override suspend fun getBestPodcasts(
            queryMap: Map<String, String>
    ): BestPodcastsListen {
        val result = api.getBestPodcasts(queryMap)
        val data = result.body()
        if (result.isSuccessful && data != null) return data
        else throw RequestUnsuccessfulException()
    }

    override suspend fun getPodcastDetails(queryMap: Map<String, String>): ChannelListen {
        val result = api.getPodcastDetails(queryMap)
        val data = result.body()
        if (result.isSuccessful && data != null) return data
        else throw RequestUnsuccessfulException()
    }
}