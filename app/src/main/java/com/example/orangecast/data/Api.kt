package com.example.orangecast.data

import com.example.orangecast.entity.SearchResult
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface Api {

    @GET("/search")
    fun search(@QueryMap parameters: Map<String, String>): Single<Response<SearchResult>>

}