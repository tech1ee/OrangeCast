package com.example.orangecast.data

class MediaItem(
    val wrapperType: String?,
    val kind: String?,
    val artistId: String?,
    val collectionId: String?,
    val trackId: String?,
    val artistName: String?,
    val collectionName: String?,
    val trackName: String?,
    val collectionCensoredName: String?,
    val trackCensoredName: String?,
    val artistViewUrl: String?,
    val collectionViewUrl: String?,
    val feedUrl: String?,
    val trackViewUrl: String?,
    val artworkUrl30: String?,
    val artworkUrl60: String?,
    val artworkUrl100: String?,
    val genreIds: List<String>?,
    val genres: List<String>?,
    val primaryGenreName: String?
)