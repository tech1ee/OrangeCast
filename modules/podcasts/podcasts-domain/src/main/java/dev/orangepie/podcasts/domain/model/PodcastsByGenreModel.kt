package dev.orangepie.podcasts.domain.model

import dev.orangepie.genres.domain.model.PodcastGenreModel

data class PodcastsByGenreModel(
    val genre: PodcastGenreModel,
    val podcasts: List<PodcastChannelModel>
)
