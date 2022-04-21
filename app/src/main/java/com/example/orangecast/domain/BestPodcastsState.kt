package com.example.orangecast.domain

import com.example.orangecast.entity.Channel

sealed class BestPodcastsState {
    object Loading: BestPodcastsState()
    data class Data(val list: List<Channel>): BestPodcastsState()
    data class Error(val e: Exception): BestPodcastsState()
}