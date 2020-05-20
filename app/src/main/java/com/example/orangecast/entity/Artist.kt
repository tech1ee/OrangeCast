package com.example.orangecast.entity

class Artist(
    val kind: String? = null,
    val artistId: String? = null,
    val artistName: String? = null,
    val collectionName: String? = null,
    val artistViewUrl: String? = null,
    val feedUrl: String? = null,
    val artworkUrl30: String? = null,
    val artworkUrl60: String? = null,
    val artworkUrl100: String? = null,
    val genreIds: List<String>? = null,
    val genres: List<String>? = null,
    val primaryGenreName: String? = null,
    var isSubscribed: Boolean = false,
    var feed: Feed? = null
)