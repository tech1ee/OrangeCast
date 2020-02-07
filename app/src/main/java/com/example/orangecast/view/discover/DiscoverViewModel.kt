package com.example.orangecast.view.discover

import androidx.lifecycle.MutableLiveData
import com.example.orangecast.BaseViewModel
import com.example.orangecast.data.ArtistsByGenre
import com.example.orangecast.interactor.GenresInteractor

class DiscoverViewModel(
    private val interactor: GenresInteractor
) : BaseViewModel() {

    var searchText = ""
    private val discoverLiveData = MutableLiveData<List<ArtistsByGenre>>()

    fun getDiscoverLiveData() = discoverLiveData

    fun discover() {
        disposable.add(
            interactor.fetchAllGenres()
                .subscribe(
                    {
                        discoverLiveData.apply { this.value = it }
                    },
                    {

                    })
        )
    }

    fun search() {

    }



}