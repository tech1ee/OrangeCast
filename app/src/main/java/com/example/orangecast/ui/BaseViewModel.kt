package com.example.orangecast.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel : ViewModel() {

    private val eventLiveData = MutableLiveData<ViewEvent>()
    protected val disposable = CompositeDisposable()

    fun getEventLiveData(): LiveData<ViewEvent> {
        return eventLiveData
    }

    protected fun <T> Single<T>.subscribeWithMapToEvent() {
        showEvent(ViewEvent.Progress(true))
        disposable.add(
            this
                .subscribe(
                    { data ->
                        showEvent(ViewEvent.Data<T>(data))
                        showEvent(ViewEvent.Progress(false))
                    },
                    { error ->
                        showEvent(
                            ViewEvent.Error(error.localizedMessage ?: "Error", error))
                        showEvent(ViewEvent.Progress(false))
                        error.printStackTrace()
                    }
                )
        )
    }

    protected fun showEvent(event: ViewEvent) {
        eventLiveData.apply { this.value = event }
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}