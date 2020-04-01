package com.example.orangecast.data.api

import com.example.orangecast.entity.RSSChannel
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface FeedApi {

    @GET
    fun getFeed(@Url url: String): Single<Response<RSSChannel>>
}