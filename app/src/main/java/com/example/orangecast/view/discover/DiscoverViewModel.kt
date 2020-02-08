package com.example.orangecast.view.discover

import androidx.lifecycle.MutableLiveData
import com.example.orangecast.BaseViewModel
import com.example.orangecast.data.ArtistsByGenre
import com.example.orangecast.interactor.GenresInteractor

class DiscoverViewModel(
    private val interactor: GenresInteractor
) : BaseViewModel() {

    var searchText = ""

    fun getDiscoverLiveData() = eventLiveData

    fun discover() {
        interactor.fetchAllGenres().subscribeWithMapToEvent()
    }

    fun search() {

    }


}