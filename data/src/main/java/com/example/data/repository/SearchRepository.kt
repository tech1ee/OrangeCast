package com.example.data.repository

import com.example.data.network.Api
import com.example.data.network.entity.ArtistResponse
import com.example.data.network.entity.SearchResultResponse
import io.reactivex.Single

class SearchRepository(
    private val searchApi: Api
) : Repository {

    private var discoverData: SearchResultResponse? = null

    fun discover(isRefresh: Boolean, parameters: Map<String, String>): Single<SearchResultResponse> {
        return if (isRefresh || discoverData == null) {
            searchApi.search(parameters)
                .subscribeToResponse()
                .doAfterSuccess {
                    discoverData = it
                    discoverData
                }
        } else Single.just(discoverData)
    }

    fun getChannelByFeedUrl(feedUrl: String): ArtistResponse? {
        return discoverData?.results?.find { it.feedUrl == feedUrl }
    }
}