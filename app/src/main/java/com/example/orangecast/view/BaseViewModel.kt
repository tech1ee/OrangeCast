package com.example.orangecast.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.orangecast.entity.ViewEvent
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel : ViewModel() {

    protected val eventLiveData = MutableLiveData<ViewEvent>()
    private val disposable = CompositeDisposable()

    fun getEventLiveData(): LiveData<ViewEvent> {
        return eventLiveData
    }

    protected fun <T> Single<T>.subscribeWithMapToEvent() {
        eventLiveData.apply { this.value = ViewEvent.Progress<T>(true) }
        disposable.add(
            this
                .subscribe(
                    { data ->
                        eventLiveData.apply { this.value = ViewEvent.Data(data) }
                        eventLiveData.apply { this.value = ViewEvent.Progress<T>(false) }
                    },
                    { error ->
                        eventLiveData.apply { this.value = ViewEvent.Error<T>(error.localizedMessage ?: "Error", error) }
                        eventLiveData.apply { this.value = ViewEvent.Progress<T>(false) }
                        error.printStackTrace()
                    }
                )
        )
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}