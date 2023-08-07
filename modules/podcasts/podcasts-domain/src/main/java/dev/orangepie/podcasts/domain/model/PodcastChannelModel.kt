package dev.orangepie.podcasts.domain.model

import dev.orangepie.genres.domain.model.PodcastGenreModel

data class PodcastChannelModel(
    val itunesId: Long?,
    val title: String?,
    val imagePreviewUrl: String?,
    val imageFullUrl: String?,
    val genres: List<PodcastGenreModel> = emptyList(),
)
