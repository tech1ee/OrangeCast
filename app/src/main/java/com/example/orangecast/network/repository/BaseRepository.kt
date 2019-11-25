package com.example.orangecast.network.repository

import com.example.orangecast.data.ErrorResponse
import com.example.orangecast.network.Event
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import retrofit2.Response

abstract class BaseRepository {

    protected fun <T> Single<Response<T>>.subscribeWithMapToEvent(): Observable<Event<T>> {
        return PublishSubject.create { emitter ->
            emitter.onNext(Event.Progress(true))
            this
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        emitter.onNext(Event.Progress(false))
                        emitter.onNext(getEvent(response))
                        emitter.onComplete()
                    },
                    { error ->
                        emitter.onNext(Event.Progress(false))
                        emitter.onNext(Event.Error(error.localizedMessage ?: "Error", error))
                        emitter.onComplete()
                    }
                )
        }
    }

    private fun <T> getEvent(response: Response<T>): Event<T> {
        return if (response.isSuccessful && response.body() != null) Event.Success(response.body()!!)
        else {
            Event.Error(
                response.message() ?: "Body is null or empty",
                Gson().fromJson(response.errorBody().toString(), ErrorResponse::class.java)
            )
        }
    }
}