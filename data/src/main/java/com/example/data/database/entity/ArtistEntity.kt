package com.example.data.database.entity

import androidx.room.Entity

@Entity(tableName = "ARTIST", primaryKeys = ["artistId", "artistName"])
class ArtistEntity(
    val artistId: String,
    val artistName: String,
    val wrapperType: String?,
    val kind: String?,
    val collectionName: String?,
    val trackName: String?,
    val artistViewUrl: String?,
    val collectionViewUrl: String?,
    val feedUrl: String?,
    val artworkUrl30: String?,
    val artworkUrl60: String?,
    val artworkUrl100: String?
)