package com.example.orangecast.ui.details

import com.example.orangecast.entity.Channel

data class PodcastDetailsViewState(
    val loading:Boolean = false,
    val data: Channel? = null
)
