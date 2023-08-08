package dev.orangepie.details.ui.mapper

import dev.orangepie.details.domain.model.PodcastDetailsModel
import dev.orangepie.details.ui.model.PodcastDetailsUIModel
import dev.orangepie.details.ui.model.PodcastDetailsUIState
import dev.orangepie.details.ui.model.PodcastDetailsViewModelState
import javax.inject.Inject

class PodcastDetailsUIMapper @Inject constructor(
    private val feedMapper: PodcastRSSFeedUIMapper,
) {

    suspend fun toUIState(state: PodcastDetailsViewModelState): PodcastDetailsUIState {
        return toUIStateBlocking(state)
    }

    fun toUIStateBlocking(state: PodcastDetailsViewModelState): PodcastDetailsUIState {
        return when {
            state.loading -> PodcastDetailsUIState.Loading
            state.error -> PodcastDetailsUIState.Error
            state.details != null -> PodcastDetailsUIState.Details(state.details.toUIModel())
            else -> PodcastDetailsUIState.Error
        }
    }

    private fun PodcastDetailsModel.toUIModel(): PodcastDetailsUIModel {
        return PodcastDetailsUIModel(
            collectionId = collectionId,
            trackId = trackId,
            artistName = artistName,
            collectionName = collectionName,
            trackName = trackName,
            collectionViewUrl = collectionViewUrl,
            feedUrl = feedUrl,
            artworkUrl100 = artworkUrl100,
            genres = genres,
            feed = feedMapper.toUIModel(feed)
        )
    }
}