package com.example.orangecast.view.discover

import androidx.lifecycle.MutableLiveData
import com.example.orangecast.BaseViewModel
import com.example.orangecast.data.ArtistsByGenre
import com.example.orangecast.interactor.DiscoverInteractor

class DiscoverViewModel(
    private val interactor: DiscoverInteractor
) : BaseViewModel() {

    private val discoverLiveData = MutableLiveData<List<ArtistsByGenre>>()

    fun getDiscoverLiveData() = discoverLiveData

    fun discover() {
        disposable.add(
            interactor.fetchGenres()
                .subscribe(
                    {
                        discoverLiveData.apply { this.value = it }
                    },
                    {

                    })
        )
    }

}