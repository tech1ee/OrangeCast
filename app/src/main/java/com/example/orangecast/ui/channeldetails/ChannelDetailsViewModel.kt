package com.example.orangecast.ui.channeldetails

import com.example.orangecast.entity.Artist
import com.example.orangecast.interactor.ChannelInteractor
import com.example.orangecast.interactor.SubscriptionInteractor
import com.example.orangecast.ui.BaseViewModel
import com.example.orangecast.ui.ViewEvent

class ChannelDetailsViewModel(
    private val channelInteractor: ChannelInteractor,
    private val subscriptionInteractor: SubscriptionInteractor
) : BaseViewModel() {

    private var artist: Artist? = null
    private var feedUrl: String? = null

    fun setFeedUrl(url: String) {
        feedUrl = url
    }

    fun getChannelDetails() {
        if (feedUrl == null) return
        channelInteractor.getChannelDetailsWithFeed(feedUrl!!)
            .doAfterSuccess {
                artist = it
                checkIsSubscribed()
            }
            .subscribeWithMapToEvent()
    }

    fun subscribe() {
        val artist = artist ?: return
        disposable.add(
            subscriptionInteractor.addSubscription(artist)
                .subscribe(
                    {
                        showEvent(ViewEvent.Data(ArtistViewEvent.Subscribed(true)))
                    },
                    {
                        showEvent(ViewEvent.Error(it.localizedMessage ?: "", it))
                    }
                )
        )
    }

    fun unsubscribe() {
        val artist = artist ?: return
        disposable.add(
            subscriptionInteractor.deleteSubscription(artist)
                .subscribe(
                    {
                        showEvent(ViewEvent.Data(ArtistViewEvent.Subscribed(false)))
                    },
                    {
                        showEvent(ViewEvent.Error(it.localizedMessage ?: "", it))
                    }
                )
        )
    }

    private fun checkIsSubscribed() {
        val artist = artist ?: return
        disposable.add(
            subscriptionInteractor.isSubscribed(artist)
                .subscribe(
                    {
                        showEvent(ViewEvent.Data(ArtistViewEvent.Subscribed(it)))
                    },
                    {
                        showEvent(ViewEvent.Error(it.localizedMessage ?: "", it))
                    }
                )
        )
    }
}