package com.example.orangecast.network.repository

import com.example.orangecast.data.SearchResult
import com.example.orangecast.network.Api
import io.reactivex.Single

class Repository(private val api: Api): BaseRepository() {

    fun search(parameters: Map<String, String>): Single<SearchResult> {
        return api.search(parameters).subscribeToResponse()
    }

}