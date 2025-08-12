package dev.orangecast.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Podcast(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val author: String,
    val category: String = "",
    val language: String = "en",
    val isExplicit: Boolean = false,
    val episodeCount: Int = 0,
    val lastUpdated: Long = 0L
)