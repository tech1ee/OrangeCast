package com.example.orangecast.ui.discover

sealed class DiscoverViewEvent {
    data class Progress(val inProgress: Boolean): DiscoverViewEvent()
    data class Error(val message: String): DiscoverViewEvent()
    data class Data(val data: Genres): DiscoverViewEvent()
}