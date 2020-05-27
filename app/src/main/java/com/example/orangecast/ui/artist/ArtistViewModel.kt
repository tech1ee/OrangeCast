package com.example.orangecast.ui.artist

import com.example.orangecast.entity.Artist
import com.example.orangecast.interactor.ArtistInteractor
import com.example.orangecast.interactor.SubscriptionInteractor
import com.example.orangecast.ui.BaseViewModel
import com.example.orangecast.ui.ViewEvent

class ArtistViewModel(
    private val channelInteractor: ArtistInteractor,
    private val subscriptionInteractor: SubscriptionInteractor
) : BaseViewModel() {

    private var artist: Artist? = null
    private var feedUrl: String? = null

    fun setFeedUrl(url: String) {
        feedUrl = url
    }

    fun getArtistDetails() {
        if (feedUrl == null) return
        channelInteractor.getArtistDetails(feedUrl!!)
            .doAfterSuccess {
                artist = it
                checkIsSubscribed()
            }
            .subscribeWithMapToEvent()
        channelInteractor.getArtistDetails(feedUrl!!).subscribeWithMapToEvent()
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