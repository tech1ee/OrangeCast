package dev.orangepie.podcasts.domain.model

data class PodcastChannelModel(
    val itunesId: Long?,
    val title: String?,
    val imagePreviewUrl: String?,
    val imageFullUrl: String?,
    val genre: String?
)
