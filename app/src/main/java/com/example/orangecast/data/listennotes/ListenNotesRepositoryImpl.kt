package com.example.orangecast.data.listennotes

import com.example.orangecast.data.listennotes.entity.BestPodcastsListen
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
        else throw Exception()
    }
}