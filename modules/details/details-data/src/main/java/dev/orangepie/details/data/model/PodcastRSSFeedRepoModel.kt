package dev.orangepie.details.data.model

data class PodcastRSSFeedRepoModel(
    val description: String?,
    val items: List<PodcastRSSFeedItemRepoModel>,
)

data class PodcastRSSFeedItemRepoModel(
    val title: String?,
    val description: String?,
    val link: String?,
    val pubDate: String?,
    val episode: String?,
    val season: String?,
    val itunesDuration: String?,
    val itunesSummary: String?,
)
