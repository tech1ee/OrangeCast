package dev.orangepie.library.ui

import dev.orangepie.podcasts.ui.model.PodcastChannelUIModel
import kotlinx.collections.immutable.ImmutableList

sealed class LibraryUIState {
    object Loading : LibraryUIState()
    object Error : LibraryUIState()
    object Empty : LibraryUIState()
    data class Podcasts(val podcasts: ImmutableList<PodcastChannelUIModel>) : LibraryUIState()
}