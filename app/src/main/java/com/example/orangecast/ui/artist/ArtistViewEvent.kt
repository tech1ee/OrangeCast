package com.example.orangecast.ui.artist

import com.example.orangecast.entity.Artist
import com.example.orangecast.entity.Feed

sealed class ArtistViewEvent {
    sealed class ArtistInfo: ArtistViewEvent() {
        data class Data(val artist: Artist) : ArtistViewEvent.ArtistInfo()
        data class Subscribed(val isSubscribed: Boolean): ArtistViewEvent()
        data class Progress(val inProgress: Boolean) : ArtistViewEvent.ArtistInfo()
    }
    sealed class ArtistFeed: ArtistViewEvent() {
        data class Data(val feed: Feed): ArtistViewEvent.ArtistFeed()
        data class Progress(val inProgress: Boolean): ArtistViewEvent.ArtistFeed()
        data class Error(val message: String): ArtistViewEvent.ArtistFeed()
    }
}