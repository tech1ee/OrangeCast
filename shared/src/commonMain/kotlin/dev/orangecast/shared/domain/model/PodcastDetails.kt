package dev.orangecast.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PodcastDetails(
    val podcast: Podcast,
    val episodes: List<PodcastEpisode>,
    val isSubscribed: Boolean = false
)