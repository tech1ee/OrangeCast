package dev.orangepie.podcasts.ui

import dev.orangepie.podcasts.domain.model.PodcastChannelModel

data class PodcastsViewModelState(
    val podcastsLoading: Boolean = false,
    val podcastsError: Boolean = false,
    val podcasts: List<PodcastChannelModel>? = null
)
