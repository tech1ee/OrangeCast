package com.example.orangecast.ui.artist

import androidx.lifecycle.MutableLiveData
import com.example.orangecast.interactor.ArtistInteractor
import com.example.orangecast.interactor.SubscriptionInteractor
import com.example.orangecast.ui.BaseViewModel

class ArtistViewModel(
        private val artistInteractor: ArtistInteractor,
        private val subscriptionInteractor: SubscriptionInteractor
) : BaseViewModel() {

    private val infoLiveData = MutableLiveData<ArtistViewEvent>()
    private val feedLiveData = MutableLiveData<ArtistViewEvent>()

    fun infoLiveData() = infoLiveData
    fun feedLiveData() = feedLiveData

    private var artist: Artist? = null
    private var feedUrl: String? = null

    fun setFeedUrl(url: String) {
        feedUrl = url
    }

    fun getArtistDetails() {
        if (feedUrl == null) return
        disposable.add(
                artistInteractor.getArtistDetails(feedUrl!!)
                        .subscribe({
                            artist = it
                            infoEvent(ArtistViewEvent.ArtistInfo.Data(artist!!))
                            checkIsSubscribed()
                            getArtistFeed()
                        }, {})
        )
    }

    private fun getArtistFeed() {
        feedEvent(ArtistViewEvent.ArtistFeed.Progress(true))
        disposable.add(
                artistInteractor.getArtistFeed(feedUrl!!)
                        .subscribe({
                            feedEvent(ArtistViewEvent.ArtistFeed.Progress(false))
                            feedEvent(ArtistViewEvent.ArtistFeed.Data(it))
                        }, {
                            feedEvent(ArtistViewEvent.ArtistFeed.Progress(false))
                            feedEvent(ArtistViewEvent.ArtistFeed.Error(it.localizedMessage ?: "FEED ERROR"))
                        })
        )
    }

    fun subscribe() {
        val artist = artist ?: return
        disposable.add(
                subscriptionInteractor.addSubscription(artist)
                        .subscribe({
                            infoEvent(ArtistViewEvent.ArtistInfo.Subscribed(true))
                        }, {
                            infoEvent(ArtistViewEvent.ArtistFeed.Error(it.localizedMessage ?: ""))
                        })
        )
    }

    fun unsubscribe() {
        val artist = artist ?: return
        disposable.add(
                subscriptionInteractor.deleteSubscription(artist)
                        .subscribe({
                            infoEvent(ArtistViewEvent.ArtistInfo.Subscribed(false))
                        }, {
                            infoEvent(ArtistViewEvent.ArtistFeed.Error(it.localizedMessage ?: ""))
                        })
        )
    }

    private fun checkIsSubscribed() {
        val artist = artist ?: return
        disposable.add(
                subscriptionInteractor.isSubscribed(artist)
                        .subscribe({
                            infoEvent(ArtistViewEvent.ArtistInfo.Subscribed(it))
                        }, {
                            infoEvent(ArtistViewEvent.ArtistFeed.Error(it.localizedMessage ?: ""))
                        })
        )
    }

    private fun infoEvent(event: ArtistViewEvent) {
        infoLiveData.value = event
    }

    private fun feedEvent(event: ArtistViewEvent) {
        feedLiveData.value = event
    }
}