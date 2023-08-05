package dev.orangepie.genres.domain.model

data class PodcastGenreModel(
    val id: Int,
    val name: String,
    val parentId: Int?,
)
