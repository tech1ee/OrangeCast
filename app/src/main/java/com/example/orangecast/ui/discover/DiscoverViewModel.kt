package com.example.orangecast.ui.discover

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.orangecast.ui.BaseViewModel
import com.example.orangecast.interactor.GenresInteractor

class DiscoverViewModel(
        private val interactor: GenresInteractor
) : BaseViewModel() {

    private val discoverLiveData = MediatorLiveData<DiscoverViewEvent>()

    fun discoverLiveData() = discoverLiveData

    fun discover(isRefresh: Boolean = false) {
        event(DiscoverViewEvent.Progress(true))
        disposable.add(
                interactor.fetchAllGenres(isRefresh)
                        .subscribe({
                            event(DiscoverViewEvent.Progress(false))
                            event(DiscoverViewEvent.Data(it))
                        }, {
                            event(DiscoverViewEvent.Progress(false))
                            event(DiscoverViewEvent.Error(it.localizedMessage ?: "DISCOVER ERROR"))
                        })
        )
    }

    private fun event(event: DiscoverViewEvent) {
        discoverLiveData.apply { value = event }
    }
}