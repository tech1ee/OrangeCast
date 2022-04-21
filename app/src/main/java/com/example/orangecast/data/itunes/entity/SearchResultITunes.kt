package com.example.orangecast.data.itunes.entity

import com.example.orangecast.data.itunes.entity.ChannelITunes

data class SearchResultITunes(
    val resultCount: Int,
    val results: List<ChannelITunes>
)
