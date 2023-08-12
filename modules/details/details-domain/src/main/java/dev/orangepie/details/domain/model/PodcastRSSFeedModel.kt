package dev.orangepie.details.domain.model

data class PodcastRSSFeedModel(
    val description: String?,
    val items: List<PodcastRSSFeedItemModel>,
)

data class PodcastRSSFeedItemModel(
    val title: String?,
    val description: String?,
    val audio: String?,
    val link: String?,
    val pubDate: String?,
    val episode: String?,
    val season: String?,
    val itunesDuration: String?,
    val itunesSummary: String?,
    val isPlaying: Boolean = false,
)