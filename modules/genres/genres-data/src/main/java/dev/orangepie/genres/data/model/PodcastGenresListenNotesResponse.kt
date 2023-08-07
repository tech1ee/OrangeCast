package dev.orangepie.genres.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PodcastGenresListenNotesResponse(
    @Json(name = "genres")
    val genres: List<PodcastGenreListenNotesResponse>,
)
