package com.example.orangecast.ui.channeldetails

sealed class ArtistViewEvent {
    data class Subscribed(val isSubscribed: Boolean): ArtistViewEvent()
}