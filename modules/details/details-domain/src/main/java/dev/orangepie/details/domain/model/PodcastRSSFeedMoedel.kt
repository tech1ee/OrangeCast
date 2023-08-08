package dev.orangepie.details.domain.model

data class PodcastRSSFeedModel(
    val description: String,
    val items: List<PodcastRSSFeedItemModel>,
)

data class PodcastRSSFeedItemModel(
    val title: String,
    val description: String,
    val link: String,
    val pubDate: String,
    val enclosure: PodcastRSSFeedEnclosureModel,
    val itunesDuration: String,
    val itunesSummary: String?,
)

data class PodcastRSSFeedEnclosureModel(
    val url: String,
    val length: String,
    val type: String,
)