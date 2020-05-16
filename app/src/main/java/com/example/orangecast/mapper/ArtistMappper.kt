package com.example.orangecast.mapper

import com.example.data.database.entity.ArtistEntity
import com.example.data.network.entity.ArtistResponse
import com.example.data.network.entity.FeedResponse
import com.example.orangecast.entity.Artist
import com.example.orangecast.entity.Feed

fun List<ArtistResponse>.mapResponseToAppEntity(): List<Artist> {
    val list = arrayListOf<Artist>()
    this.forEach { list.add(it.mapResponseToAppEntity()) }
    return list
}

fun ArtistResponse.mapResponseToAppEntity(feedResponse: FeedResponse? = null): Artist {
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
        feed = feedResponse?.mapResponseToAppEntity()
    )
}

fun FeedResponse.mapResponseToAppEntity(): Feed {
    return Feed(
        description = description, episodes = episodes.mapResponseToAppEntity()
    )
}

fun List<ArtistEntity>.mapDatabaseToAppEntity(): List<Artist> {
    return this.map {
        Artist(
            artistId = it.artistId,
            artistName = it.artistName,
            kind = it.kind,
            collectionName = it.collectionName,
            artworkUrl30 = it.artworkUrl30,
            artworkUrl60 = it.artworkUrl60,
            artworkUrl100 = it.artworkUrl100,
            artistViewUrl = it.artistViewUrl,
            feedUrl = it.feedUrl
        )
    }
}