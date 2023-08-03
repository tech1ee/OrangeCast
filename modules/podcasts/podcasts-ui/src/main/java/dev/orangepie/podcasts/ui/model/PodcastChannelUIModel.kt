package dev.orangepie.podcasts.ui.model

data class PodcastChannelUIModel(
    val itunesId: Long?,
    val title: String?,
    val imagePreviewUrl: String?,
    val imageFullUrl: String?,
    val genre: String?
)