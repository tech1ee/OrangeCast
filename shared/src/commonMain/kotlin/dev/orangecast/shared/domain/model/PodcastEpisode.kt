package dev.orangecast.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PodcastEpisode(
    val id: String,
    val title: String,
    val description: String,
    val audioUrl: String,
    val thumbnailUrl: String,
    val duration: Long,
    val publishedAt: Long,
    val isPlayed: Boolean = false,
    val playbackPosition: Long = 0L
)