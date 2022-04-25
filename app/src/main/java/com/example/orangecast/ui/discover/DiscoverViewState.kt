package com.example.orangecast.ui.discover

import com.example.orangecast.entity.Channel

data class DiscoverViewState(
    val bestPodcasts: List<Channel> = emptyList(),
    val bestPodcastsLoading: Boolean = false
)