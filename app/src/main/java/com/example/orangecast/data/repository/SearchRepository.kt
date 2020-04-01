package com.example.orangecast.data.repository

import com.example.orangecast.data.Repository
import com.example.orangecast.entity.SearchResult
import com.example.orangecast.data.api.SearchApi
import com.example.orangecast.di.module.ApiModule
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Named

class SearchRepository @Inject constructor(
    @Named(ApiModule.ITUNES) private val searchApi: SearchApi):
    Repository {

    fun search(parameters: Map<String, String>): Single<SearchResult> {
        return searchApi.search(parameters).subscribeToResponse()
    }
}