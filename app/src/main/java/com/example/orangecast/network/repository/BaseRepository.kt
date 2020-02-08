package com.example.orangecast.network.repository

import com.example.orangecast.data.ErrorResponse
import com.google.gson.Gson
import io.reactivex.Single
import retrofit2.Response

abstract class BaseRepository {

    protected fun <T> Single<Response<T>>.subscribeToResponse(): Single<T> {
        return this
            .map {
                if (it.isSuccessful && it.body() != null) it.body()!!
                else throw Throwable(Gson().fromJson(it.errorBody().toString(), ErrorResponse::class.java).message)
            }
    }
}