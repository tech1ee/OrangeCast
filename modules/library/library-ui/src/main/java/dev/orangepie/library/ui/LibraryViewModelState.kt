package dev.orangepie.library.ui

import dev.orangepie.podcasts.domain.model.PodcastChannelModel

data class LibraryViewModelState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val podcasts: List<PodcastChannelModel> = emptyList(),
)
