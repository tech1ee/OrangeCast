package com.example.orangecast.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.orangecast.entity.ViewEvent
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel : ViewModel() {

    private val eventLiveData = MutableLiveData<ViewEvent>()
    private val disposable = CompositeDisposable()

    fun getEventLiveData(): LiveData<ViewEvent> {
        return eventLiveData
    }

    protected fun <T> Single<T>.subscribeWithMapToEvent() {
        showEvent(ViewEvent.Progress<T>(true))
        disposable.add(
            this
                .subscribe(
                    { data ->
                        showEvent(ViewEvent.Data<T>(data))
                        showEvent(ViewEvent.Progress<T>(false))
                    },
                    { error ->
                        showEvent(ViewEvent.Error<T>(error.localizedMessage ?: "Error", error))
                        showEvent(ViewEvent.Progress<T>(false))
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