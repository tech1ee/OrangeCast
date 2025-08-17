package dev.orangecast.shared.domain.player

import dev.orangecast.shared.domain.model.PodcastEpisode

sealed class PlayerEvent(open val episode: PodcastEpisode? = null) {
    data class Loading(override val episode: PodcastEpisode?) : PlayerEvent()
    data class Playing(override val episode: PodcastEpisode?) : PlayerEvent()
    data class Paused(override val episode: PodcastEpisode?) : PlayerEvent()
    data class Stopped(override val episode: PodcastEpisode?) : PlayerEvent()
    data class Error(override val episode: PodcastEpisode?, val message: String) : PlayerEvent()
}