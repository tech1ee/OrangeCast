package com.example.orangecast.mapper

import com.example.data.network.entity.EpisodeResponse
import com.example.orangecast.entity.Episode

fun List<EpisodeResponse>?.mapResponseToAppEntity(): List<Episode> {
    val list = arrayListOf<Episode>()
    this?.forEach { list.add(it.mapResponseToAppEntity()) }
    return list
}

fun EpisodeResponse?.mapResponseToAppEntity(): Episode {
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