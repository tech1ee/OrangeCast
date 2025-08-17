package dev.orangecast.shared.domain.model

data class Genre(
    val id: Int,
    val name: String,
    val parentId: Int? = null,
    val description: String? = null
)