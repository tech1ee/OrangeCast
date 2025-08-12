package dev.orangecast.shared.data.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ITunesSearchResponse(
    val resultCount: Int,
    val results: List<ITunesPodcast>
)

@Serializable
data class ITunesPodcast(
    val trackId: Long,
    val collectionId: Long? = null,
    val trackName: String,
    val collectionName: String? = null,
    val artistName: String,
    val artworkUrl100: String? = null,
    val artworkUrl600: String? = null,
    val feedUrl: String? = null,
    val trackCount: Int? = null,
    val primaryGenreName: String? = null,
    val contentAdvisoryRating: String? = null,
    val country: String? = null,
    val languageCodesISO2A: List<String>? = null
)