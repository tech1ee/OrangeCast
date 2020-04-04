package com.example.orangecast.view.discover

import com.example.orangecast.entity.ArtistsByGenre
import com.example.orangecast.view.BaseViewModel
import com.example.orangecast.interactor.GenresInteractor

class DiscoverViewModel(
    private val interactor: GenresInteractor
) : BaseViewModel() {

    var searchText = ""

    fun discover() {
        interactor.fetchAllGenres().subscribeWithMapToEvent()
    }

    fun search() {

    }


}