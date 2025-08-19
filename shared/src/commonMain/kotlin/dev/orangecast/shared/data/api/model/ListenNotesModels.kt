package dev.orangecast.shared.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListenNotesSearchResponse(
    @SerialName("count") val count: Int,
    @SerialName("total") val total: Int,
    @SerialName("next_offset") val nextOffset: Int?,
    @SerialName("results") val results: List<ListenNotesPodcast>
)

@Serializable 
data class ListenNotesPodcast(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("publisher") val publisher: String,
    @SerialName("image") val image: String,
    @SerialName("thumbnail") val thumbnail: String? = null,
    @SerialName("website") val website: String? = null,
    @SerialName("total_episodes") val totalEpisodes: Int,
    @SerialName("explicit_content") val explicitContent: Boolean,
    @SerialName("genre_ids") val genreIds: List<Int>,
    @SerialName("itunes_id") val itunesId: Long? = null,
    @SerialName("language") val language: String? = null,
    @SerialName("country") val country: String? = null,
    @SerialName("rss") val rss: String? = null,
    @SerialName("latest_pub_date_ms") val latestPubDateMs: Long? = null,
    @SerialName("earliest_pub_date_ms") val earliestPubDateMs: Long? = null,
    @SerialName("listennotes_url") val listennotesUrl: String? = null,
    @SerialName("audio_length_sec") val audioLengthSec: Int? = null,
    @SerialName("update_frequency_hours") val updateFrequencyHours: Int? = null,
    // PRO plan fields - могут содержать строку с сообщением об ограничениях
    @SerialName("listen_score") val listenScore: String? = null,
    @SerialName("listen_score_global_rank") val listenScoreGlobalRank: String? = null,
    @SerialName("latest_episode_id") val latestEpisodeId: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("is_claimed") val isClaimed: Boolean? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("extra") val extra: Map<String, String>? = null,
    @SerialName("looking_for") val lookingFor: Map<String, Boolean>? = null,
    @SerialName("has_guest_interviews") val hasGuestInterviews: Boolean? = null,
    @SerialName("has_sponsors") val hasSponsors: Boolean? = null,
    @SerialName("episodes") val episodes: List<ListenNotesEpisode>? = null
)

@Serializable
data class ListenNotesEpisode(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("pub_date_ms") val pubDateMs: Long,
    @SerialName("audio") val audio: String,
    @SerialName("audio_length_sec") val audioLengthSec: Int,
    @SerialName("explicit_content") val explicitContent: Boolean,
    @SerialName("image") val image: String?,
    @SerialName("link") val link: String?
)

@Serializable
data class ListenNotesBestPodcastsResponse(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("parent_id") val parentId: Int?,
    @SerialName("podcasts") val podcasts: List<ListenNotesPodcast>
)

@Serializable
data class ListenNotesGenresResponse(
    @SerialName("genres") val genres: List<ListenNotesGenre>
)

@Serializable
data class ListenNotesGenre(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("parent_id") val parentId: Int?
)