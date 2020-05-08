package com.example.orangecast.mapper

import com.example.data.network.entity.ArtistResponse
import com.example.data.network.entity.FeedResponse
import com.example.orangecast.entity.Artist
import com.example.orangecast.entity.Feed

fun List<ArtistResponse>.mapToAppEntity(): List<Artist> {
    val list = arrayListOf<Artist>()
    this.forEach { list.add(it.mapToAppEntity()) }
    return list
}

fun ArtistResponse.mapToAppEntity(feedResponse: FeedResponse? = null): Artist {
    return Artist(
        kind = kind,
        artistId = artistId,
        artistName = artistName,
        collectionName = collectionName,
        artistViewUrl = artistViewUrl,
        feedUrl = feedUrl,
        artworkUrl30 = artworkUrl30,
        artworkUrl60 = artworkUrl60,
        artworkUrl100 = artworkUrl100,
        genreIds = genreIds,
        genres = genres,
        primaryGenreName = primaryGenreName,
        feed = feedResponse?.mapToAppEntity()
    )
}

fun FeedResponse.mapToAppEntity(): Feed {
    return Feed(
        description = description,
        episodes = episodes.mapToAppEntity()
    )
}