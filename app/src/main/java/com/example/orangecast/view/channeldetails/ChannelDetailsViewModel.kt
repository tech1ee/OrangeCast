package com.example.orangecast.view.channeldetails

import com.example.orangecast.entity.Channel
import com.example.orangecast.entity.ViewEvent
import com.example.orangecast.interactor.ChannelInteractor
import com.example.orangecast.view.BaseViewModel

class ChannelDetailsViewModel(
    private val channelInteractor: ChannelInteractor
): BaseViewModel() {

   fun getChannelDetails(feedUrl: String) {
        channelInteractor.getChannelDetailsWithFeed(feedUrl)
            .subscribeWithMapToEvent()
    }
}