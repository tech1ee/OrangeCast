package com.example.orangecast.data.listennotes

data class ChannelListen(
        val id: String?,
        val title: String?,
        val publisher: String?,
        val image: String?,
        val thumbnail: String,
        val totalEpisodes: Int?,
        val explicitContent: Boolean?,
        val description: String?,
        val itunesId: Int?,
        val language: String?,
        val country: String?,
        val website: String?,
        val is_claimed: String?,
        val genre_ids: List<Int>?
)