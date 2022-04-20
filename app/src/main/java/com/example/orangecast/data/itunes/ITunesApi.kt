package com.example.orangecast.data.itunes

import com.example.orangecast.data.itunes.SearchResultITunes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ITunesApi {

    @GET("/search?parameterkeyvalue")
    suspend fun search(@QueryMap queryMap: Map<String, String>): Response<SearchResultITunes>
}