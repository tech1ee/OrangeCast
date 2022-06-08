package com.example.orangecast.domain

import com.example.orangecast.data.listennotes.entity.ChannelListen
import com.example.orangecast.entity.Channel

fun List<ChannelListen>.toUIChannel(): List<Channel> {
    val list = mutableListOf<Channel>()
    forEach {
        list.add(
            it.toUIChannel()
        )
    }
    return list
}

fun ChannelListen.toUIChannel(): Channel {
    return Channel(
        idListenNotes = this.id,
        idItunes = this.itunes_id,
        title = this.title,
        image = this.image,
        thumbnail = this.thumbnail,
        description = this.description,
        website = this.website,
        genreIds = this.genre_ids
    )
}