package dev.orangepie.podcasts.ui

import dev.orangepie.genres.ui.mapper.PodcastGenresUiMapper
import dev.orangepie.podcasts.domain.model.PodcastChannelModel
import dev.orangepie.podcasts.ui.model.PodcastChannelUIModel
import dev.orangepie.podcasts.ui.model.PodcastsByGenreUiModel
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class PodcastsUIMapper @Inject constructor(
    private val genresMapper: PodcastGenresUiMapper
) {

    suspend fun toUIState(state: PodcastsViewModelState): PodcastsUIState {
        return toUIStateBlocking(state)
    }

    fun toUIStateBlocking(state: PodcastsViewModelState): PodcastsUIState {
        return when {
            state.podcastsLoading -> PodcastsUIState.Loading
            state.podcastsError -> PodcastsUIState.Error
            state.podcasts.isNullOrEmpty() -> PodcastsUIState.Empty
            else -> PodcastsUIState.PodcastsByGenre(
                podcasts = state.podcasts.map {
                    PodcastsByGenreUiModel(
                        genre = genresMapper.toUIModel(it.genre),
                        podcasts = it.podcasts.map { podcast ->
                            podcast.toUIModel()
                        }.toImmutableList()
                    )
                }.toImmutableList()
            )
        }
    }


    private fun PodcastChannelModel.toUIModel(): PodcastChannelUIModel {
        return PodcastChannelUIModel(
            itunesId = itunesId,
            title = title,
            imagePreviewUrl = imagePreviewUrl,
            imageFullUrl = imageFullUrl,
            genres = genres.map {
                dev.orangepie.genres.ui.model.PodcastGenreUiModel(
                    id = it.id,
                    name = it.name,
                    parentId = it.parentId,
                )
            }.toImmutableList(),
        )
    }
}