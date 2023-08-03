package dev.orangepie.podcasts.ui

import dev.orangepie.podcasts.domain.model.PodcastChannelModel
import dev.orangepie.podcasts.ui.model.PodcastChannelUIModel
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class PodcastsUIMapper @Inject constructor() {

    suspend fun toUIState(state: PodcastsViewModelState): PodcastsUIState {
        return toUIStateBlocking(state)
    }

    fun toUIStateBlocking(state: PodcastsViewModelState): PodcastsUIState {
        return when {
            state.podcastsLoading -> PodcastsUIState.Loading
            state.podcastsError -> PodcastsUIState.Error
            state.podcasts.isNullOrEmpty() -> PodcastsUIState.Empty
            else -> PodcastsUIState.Success(state.podcasts.map { toUIModel(it) }.toImmutableList())
        }
    }

    private fun toUIModel(model: PodcastChannelModel): PodcastChannelUIModel {
        return PodcastChannelUIModel(
            itunesId = model.itunesId,
            title = model.title,
            imagePreviewUrl = model.imagePreviewUrl,
            imageFullUrl = model.imageFullUrl,
            genre = model.genre
        )
    }
}