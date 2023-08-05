package dev.orangepie.podcasts.ui

import dev.orangepie.podcasts.ui.model.PodcastsByGenreUiModel
import kotlinx.collections.immutable.ImmutableList

sealed class PodcastsUIState {
    object Loading : PodcastsUIState()
    object Error : PodcastsUIState()
    object Empty : PodcastsUIState()
    data class PodcastsByGenre(val podcasts: ImmutableList<PodcastsByGenreUiModel>) : PodcastsUIState()
}
