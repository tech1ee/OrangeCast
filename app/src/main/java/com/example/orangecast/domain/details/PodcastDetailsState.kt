package com.example.orangecast.domain.details

import com.example.orangecast.entity.Channel

sealed class PodcastDetailsState {
    object Loading: PodcastDetailsState()
    data class Data(val data: Channel): PodcastDetailsState()
    data class Error(val e: Exception): PodcastDetailsState()
}