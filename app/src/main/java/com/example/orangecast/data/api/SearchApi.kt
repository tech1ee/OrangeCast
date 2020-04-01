package com.example.orangecast.data.api

import com.example.orangecast.entity.RSSChannel
import com.example.orangecast.entity.SearchResult
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface SearchApi {

    @GET("/search")
    fun search(@QueryMap parameters: Map<String, String>): Single<Response<SearchResult>>

}