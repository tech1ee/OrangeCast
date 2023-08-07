package dev.orangepie.podcasts.ui.model

import dev.orangepie.genres.ui.model.PodcastGenreUiModel
import kotlinx.collections.immutable.ImmutableList

data class PodcastsByGenreUiModel(
    val genre: PodcastGenreUiModel,
    val podcasts: ImmutableList<PodcastChannelUIModel>
)
