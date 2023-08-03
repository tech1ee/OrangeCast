package dev.orangepie.podcasts.ui

import dev.orangepie.podcasts.ui.model.PodcastChannelUIModel
import kotlinx.collections.immutable.ImmutableList

sealed class PodcastsUIState {
    object Loading : PodcastsUIState()
    object Error : PodcastsUIState()
    object Empty : PodcastsUIState()
    data class Success(val channels: ImmutableList<PodcastChannelUIModel>) : PodcastsUIState()
}
