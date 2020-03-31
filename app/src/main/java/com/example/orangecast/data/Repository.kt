package com.example.orangecast.data

import com.example.orangecast.entity.SearchResult
import com.example.orangecast.data.network.Api
import com.example.orangecast.entity.RSSChannel
import io.reactivex.Single

class Repository(private val api: Api): BaseRepository() {

    fun search(parameters: Map<String, String>): Single<SearchResult> {
        return api.search(parameters).subscribeToResponse()
    }

    fun getFeed(feedUrl: String): Single<RSSChannel> {
        return api.getFeed(feedUrl).subscribeToResponse()
    }
}