package com.example.orangecast.view.channeldetails

import com.example.orangecast.entity.Channel
import com.example.orangecast.entity.ViewEvent
import com.example.orangecast.interactor.ChannelInteractor
import com.example.orangecast.view.BaseViewModel

class ChannelDetailsViewModel(
    private val channelInteractor: ChannelInteractor
): BaseViewModel() {

    private var channel: Channel? = null

    fun getArtistDetails(feedUrl: String) {
        getChannelFeed(feedUrl)
        getChannelDetails(feedUrl)
    }

    private fun getChannelFeed(feedUrl: String) {
        channelInteractor.getChannelFeed(feedUrl)
            .subscribeWithMapToEvent()
    }

    private fun getChannelDetails(feedUrl: String) {
        channel = channelInteractor.getChannelDetails(feedUrl)
        showEvent(ViewEvent.Data(channel))
    }
}