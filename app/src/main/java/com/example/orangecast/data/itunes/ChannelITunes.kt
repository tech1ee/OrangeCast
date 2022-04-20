package com.example.orangecast.data.itunes

data class ChannelITunes(
        val wrapperType: String?,
        val kind: String?,
        val artistId: Long?,
        val collectionId: Long?,
        val trackId: Long?,
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
        val collectionPrice: Double?,
        val trackPrice: Double?,
        val trackRentalPrice: Int?,
        val collectionHdPrice: Int?,
        val trackHdPrice: Int?,
        val trackHdRentalPrice: Int?,
        val releaseDate: String?,
        val collectionExplicitness: String?,
        val trackExplicitness: String?,
        val trackCount: Int?,
        val country: String?,
        val currency: String?,
        val primaryGenreName: String?,
        val contentAdvisoryRating: String?,
        val artworkUrl600: String?,
        val genreIds: List<String>?,
        val genres: List<String>?
)
