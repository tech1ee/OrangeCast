package com.example.orangecast.view.channeldetails

import com.example.orangecast.interactor.RSSInteractor
import com.example.orangecast.view.BaseViewModel

class ChannelDetailsViewModel(
    private val rssInteractor: RSSInteractor
): BaseViewModel() {

    fun getArtistDetails(detailsUrl: String) {
        rssInteractor.getEpisodesFromRSS(detailsUrl).subscribeWithMapToEvent()
    }
}