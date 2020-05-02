package com.example.orangecast.ui.discover

import com.example.orangecast.ui.BaseViewModel
import com.example.orangecast.interactor.GenresInteractor

class DiscoverViewModel(
    private val interactor: GenresInteractor
) : BaseViewModel() {

    var searchText = ""

    fun discover(isRefresh: Boolean = false) {
        interactor.fetchAllGenres(isRefresh).subscribeWithMapToEvent()
    }

    fun search() {

    }


}