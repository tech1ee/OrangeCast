package com.example.orangecast.ui.library

import androidx.lifecycle.MutableLiveData
import com.example.orangecast.interactor.SubscriptionInteractor
import com.example.orangecast.ui.BaseViewModel

class LibraryViewModel(
        private val subscriptionInteractor: SubscriptionInteractor
) : BaseViewModel() {

    private val libraryLiveData = MutableLiveData<LibraryViewEvent>()

    fun libraryLiveData() = libraryLiveData

    fun getAllSubscriptions() {
        event(LibraryViewEvent.Progress(true))
        disposable.add(subscriptionInteractor.getAllSubscriptions()
                .subscribe({
                    event(LibraryViewEvent.Progress(false))
                    event(LibraryViewEvent.Data(it))
                }, {
                    event(LibraryViewEvent.Progress(false))
                    event(LibraryViewEvent.Error(it.localizedMessage ?: "Get subscriptions error"))
                })
        )
    }

    private fun event(event: LibraryViewEvent) {
        libraryLiveData.postValue(event)
    }
}