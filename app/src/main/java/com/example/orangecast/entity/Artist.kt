package com.example.orangecast.entity

class Artist(
    val kind: String?,
    val artistId: String?,
    val artistName: String?,
    val collectionName: String?,
    val artistViewUrl: String?,
    val feedUrl: String?,
    val artworkUrl30: String?,
    val artworkUrl60: String?,
    val artworkUrl100: String?,
    val genreIds: List<String>?,
    val genres: List<String>?,
    val primaryGenreName: String?,
    var feed: Feed? = null
)