package com.example.orangecast.network.repository

import com.example.orangecast.data.SearchResult
import com.example.orangecast.network.Api
import com.example.orangecast.network.Event
import io.reactivex.Single

class Repository(val api: Api): BaseRepository() {

    fun search(parameters: Map<String, String>): Single<Event<SearchResult>> {
        return api.search(parameters).subscribeWithMap()
    }

}