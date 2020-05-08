package com.example.orangecast.mapper

import com.example.data.network.entity.EpisodeResponse
import com.example.orangecast.entity.Episode

fun List<EpisodeResponse>?.mapToAppEntity(): List<Episode> {
    val list = arrayListOf<Episode>()
    this?.forEach { list.add(it.mapToAppEntity()) }
    return list
}

fun EpisodeResponse?.mapToAppEntity(): Episode {
    return Episode(
        title = this?.title,
        link = this?.link,
        pubDate = this?.pubDate,
        description = this?.description,
        duration = this?.duration,
        image = this?.image,
        episodeNumber = this?.episodeNumber
    )
}