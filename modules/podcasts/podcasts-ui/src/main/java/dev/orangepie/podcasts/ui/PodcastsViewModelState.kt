package dev.orangepie.podcasts.ui

import dev.orangepie.podcasts.domain.model.PodcastsByGenreModel

data class PodcastsViewModelState(
    val podcastsLoading: Boolean = false,
    val podcastsError: Boolean = false,
    val podcasts: List<PodcastsByGenreModel>? = null
)
