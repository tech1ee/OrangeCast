package com.example.orangecast.view.channeldetails

import com.example.orangecast.interactor.ChannelInteractor
import com.example.orangecast.view.BaseViewModel

class ChannelDetailsViewModel(private val channelInteractor: ChannelInteractor
) : BaseViewModel() {

    private var feedUrl: String? = null

    fun setFeedUrl(url: String) {
        feedUrl = url
    }

    fun getChannelDetails() {
        if (feedUrl == null) return

        channelInteractor.getChannelDetailsWithFeed(feedUrl!!).subscribeWithMapToEvent()
    }
}