package com.example.orangecast.data.repository

import com.example.orangecast.data.Api
import com.example.orangecast.data.Repository
import com.example.orangecast.entity.SearchResult
import io.reactivex.Single

class SearchRepository(
    private val searchApi: Api
):
    Repository {

    fun search(parameters: Map<String, String>): Single<SearchResult> {
        return searchApi.search(parameters).subscribeToResponse()
    }
}