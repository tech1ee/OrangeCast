package dev.orangepie.podcasts.ui.model

import dev.orangepie.genres.ui.model.PodcastGenreUiModel

data class PodcastChannelUIModel(
    val itunesId: Long?,
    val title: String?,
    val imagePreviewUrl: String?,
    val imageFullUrl: String?,
    val genres: List<PodcastGenreUiModel> = emptyList(),
)