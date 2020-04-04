package com.example.orangecast.view.channeldetails

import com.example.orangecast.entity.EpisodeList
import com.example.orangecast.interactor.FeedInteractor
import com.example.orangecast.view.BaseViewModel

class ChannelDetailsViewModel(
    private val feedInteractor: FeedInteractor
): BaseViewModel() {

    fun getArtistDetails(detailsUrl: String) {
        feedInteractor.getEpisodesFromRSS(detailsUrl)
            .subscribeWithMapToEvent()
    }
}