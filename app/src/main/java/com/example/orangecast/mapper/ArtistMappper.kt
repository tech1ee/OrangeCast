package com.example.orangecast.mapper

import com.example.data.database.entity.ArtistEntity
import com.example.data.network.entity.ArtistResponse
import com.example.data.network.entity.FeedResponse
import com.example.data.network.entity.GenreResponse
import com.example.orangecast.entity.Artist
import com.example.orangecast.entity.ArtistsGenre
import com.example.orangecast.entity.Feed

fun List<GenreResponse>.mapResponseToAppEntity(): List<ArtistsGenre> {
    val list = arrayListOf<ArtistsGenre>()

    this.forEach { genreResponse ->
        val genre = ArtistsGenre(genreResponse.genreId, genreResponse.genre)
        genreResponse.list.forEach { artistResponse ->
            genre.list.add(
                    Artist(
                            kind = artistResponse.kind,
                            artistId = artistResponse.artistId,
                            artistName = artistResponse.artistName,
                            collectionName = artistResponse.collectionName,
                            artistViewUrl = artistResponse.artistViewUrl,
                            feedUrl = artistResponse.feedUrl,
                            artworkUrl30 = artistResponse.artworkUrl30,
                            artworkUrl60 = artistResponse.artworkUrl60,
                            artworkUrl100 = artistResponse.artworkUrl100,
                            genres = artistResponse.genres,
                            primaryGenreName = artistResponse.primaryGenreName,
                            genreIds = artistResponse.genreIds,
                            isSubscribed = null
                    )
            )
        }
        list.add(genre)
    }
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
        primaryGenreName = primaryGenreName
    )
}

fun FeedResponse.mapResponseToAppEntity(): Feed {
    return Feed(
        description = description, episodes = episodes.mapResponseToAppEntity()
    )
}

fun Artist.mapAppEntityToDatabase(): ArtistEntity {
    return ArtistEntity(
        artistId = artistId ?: "",
        artistName = artistName ?: "",
        kind = kind,
        collectionName = collectionName,
        artistViewUrl = artistViewUrl,
        feedUrl = feedUrl,
        artworkUrl30 = artworkUrl30,
        artworkUrl60 = artworkUrl60,
        artworkUrl100 = artworkUrl100
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