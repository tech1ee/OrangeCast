package dev.orangecast.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PodcastEpisode(
    val id: String,
    val title: String,
    val description: String,
    val audioUrl: String,
    val thumbnailUrl: String,
    val publishedAt: Long,
    val duration: Long = 0L,
    val podcastId: String,
    val podcastTitle: String,
    val isExplicit: Boolean = false,
    val episodeNumber: Int? = null,
    val seasonNumber: Int? = null
)