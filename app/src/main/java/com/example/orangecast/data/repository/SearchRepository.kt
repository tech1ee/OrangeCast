package com.example.orangecast.data.repository

import com.example.orangecast.data.Api
import com.example.orangecast.data.Repository
import com.example.orangecast.entity.Channel
import com.example.orangecast.entity.SearchResult
import io.reactivex.Single

class SearchRepository(private val searchApi: Api
) : Repository {

    private var discoverData: SearchResult? = null

    fun discover(isRefresh: Boolean, parameters: Map<String, String>): Single<SearchResult> {
        return if (isRefresh || discoverData == null) {
            searchApi.search(parameters)
                .subscribeToResponse()
                .doAfterSuccess {
                    discoverData = it
                    discoverData
                }
        } else Single.just(discoverData)
    }

    fun getChannelByFeedUrl(feedUrl: String): Channel? {
        return discoverData?.results?.find { it.feedUrl == feedUrl }
    }
}