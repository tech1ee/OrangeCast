package dev.orangepie.details.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PodcastDetailsITunesResponse(
    @Json(name = "results")
    val results: List<PodcastDetailsResponse>
)

@JsonClass(generateAdapter = true)
data class PodcastDetailsResponse(
    @Json(name = "collectionId")
    val collectionId: Long,
    @Json(name = "trackId")
    val trackId: Long,
    @Json(name = "artistName")
    val artistName: String,
    @Json(name = "collectionName")
    val collectionName: String,
    @Json(name = "trackName")
    val trackName: String,
    @Json(name = "collectionCensoredName")
    val collectionCensoredName: String,
    @Json(name = "trackCensoredName")
    val trackCensoredName: String,
    @Json(name = "collectionViewUrl")
    val collectionViewUrl: String,
    @Json(name = "feedUrl")
    val feedUrl: String,
    @Json(name = "trackViewUrl")
    val trackViewUrl: String,
    @Json(name = "artworkUrl100")
    val artworkUrl100: String,
    @Json(name = "primaryGenreName")
    val primaryGenreName: String,
    @Json(name = "genreIds")
    val genreIds: List<String>,
    @Json(name = "genres")
    val genres: List<String>,
)
