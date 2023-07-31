package dev.orangepie.podcasts.data.model

import com.squareup.moshi.Json

data class PodcastsBestListenNotesResponse(
    @Json(name = "podcasts")
    val podcasts: List<PodcastListenNotesResponse>
)

data class PodcastListenNotesResponse(
    @Json(name = "id")
    val id: String?,
    @Json(name = "title")
    val title: String?,
    @Json(name = "publisher")
    val publisher: String?,
    @Json(name = "image")
    val image: String?,
    @Json(name = "thumbnail")
    val thumbnail: String,
    @Json(name = "totalEpisodes")
    val totalEpisodes: Int?,
    @Json(name = "explicitContent")
    val explicitContent: Boolean?,
    @Json(name = "description")
    val description: String?,
    @Json(name = "itunes_id")
    val itunesId: Int?,
    @Json(name = "language")
    val language: String?,
    @Json(name = "country")
    val country: String?,
    @Json(name = "website")
    val website: String?,
    @Json(name = "is_claimed")
    val isClaimed: Boolean?,
    @Json(name = "genre_ids")
    val genreIds: List<Int>?
)