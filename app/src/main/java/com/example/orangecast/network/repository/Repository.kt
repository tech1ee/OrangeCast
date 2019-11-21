package com.example.orangecast.network.repository

import androidx.lifecycle.LiveData
import com.example.orangecast.data.SearchResult
import com.example.orangecast.network.Api
import com.example.orangecast.network.Event

class Repository(private val api: Api): BaseRepository() {

    fun search(parameters: Map<String, String>): LiveData<Event<SearchResult>> {
        return api.search(parameters).subscribeWithLiveData()
    }
}