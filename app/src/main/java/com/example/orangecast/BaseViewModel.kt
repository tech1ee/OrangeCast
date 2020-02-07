package com.example.orangecast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.orangecast.network.Event
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel : ViewModel() {

    protected val disposable = CompositeDisposable()
    protected val progressEvent = MutableLiveData<Event.Progress>()
    protected val errorEvent = MutableLiveData<Event.Error>()

    protected fun manageEvents(event: Event) {
        when (event) {
            is Event.Progress -> progressEvent.apply { this.value = event }
            is Event.Error -> errorEvent.apply { this.value = event }
        }
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}