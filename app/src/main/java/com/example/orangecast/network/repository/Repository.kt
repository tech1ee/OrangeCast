package com.example.orangecast.network.repository

import com.example.orangecast.data.SearchResult
import com.example.orangecast.network.Api
import com.example.orangecast.network.Event
import io.reactivex.Observable

class Repository(val api: Api): BaseRepository() {

    fun search(parameters: Map<String, String>): Observable<Event<SearchResult>> {
        return api.search(parameters).subscribeWithMapToEvent()
    }

}