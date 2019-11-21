package com.example.orangecast.network.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.orangecast.data.ErrorResponse
import com.example.orangecast.network.Event
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

abstract class BaseRepository {

    protected fun <T>Single<Response<T>>.subscribeWithLiveData(): LiveData<Event<T>> {
        return LiveDataReactiveStreams.fromPublisher {
            this.subscribeOn(Schedulers.io())
                .map { getEvent(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }
    }

    private fun <T>getEvent(response: Response<T>): Event<T> {
        return  if (response.isSuccessful && response.body() != null) Event.Success(response.body()!!)
        else {
            Event.Error(response.message() ?: "Body is null or empty",
                Gson().fromJson(response.errorBody().toString(), ErrorResponse::class.java))
        }
    }
}