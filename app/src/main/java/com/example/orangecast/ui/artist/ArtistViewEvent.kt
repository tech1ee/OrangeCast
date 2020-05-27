package com.example.orangecast.ui.artist

sealed class ArtistViewEvent {
    data class Subscribed(val isSubscribed: Boolean): ArtistViewEvent()
}