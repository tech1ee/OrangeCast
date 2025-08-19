package dev.orangecast.shared.domain.model

data class Genre(
    val id: Int,
    val name: String,
    val parentId: Int?
)

data class GenreSection(
    val genre: Genre,
    val podcasts: List<Podcast>
)