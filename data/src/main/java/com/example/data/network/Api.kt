package com.example.data.network

import com.example.data.network.entity.SearchResultResponse
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface Api {

    @GET("/search")
    fun search(@QueryMap parameters: Map<String, String>): Single<Response<SearchResultResponse>>

}