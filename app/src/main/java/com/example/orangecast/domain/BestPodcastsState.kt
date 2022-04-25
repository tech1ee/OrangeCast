package com.example.orangecast.domain

import com.example.orangecast.entity.Channel

sealed class BestPodcastsState {
    object None: BestPodcastsState()
    object Loading: BestPodcastsState()
    data class Data(val data: List<Channel>): BestPodcastsState()
    data class Error(val e: Exception): BestPodcastsState()
}