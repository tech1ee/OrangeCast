package com.example.orangecast.data.listennotes.entity

data class ChannelListen(
        val id: String?,
        val title: String?,
        val publisher: String?,
        val image: String?,
        val thumbnail: String,
        val totalEpisodes: Int?,
        val explicitContent: Boolean?,
        val description: String?,
        val itunes_id: Int?,
        val language: String?,
        val country: String?,
        val website: String?,
        val is_claimed: Boolean?,
        val genre_ids: List<Int>?
)