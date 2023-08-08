package dev.orangepie.details.ui.model

data class PodcastRSSFeedUIModel(
    val description: String,
    val items: List<PodcastRSSFeedItemUIModel>,
)

data class PodcastRSSFeedImageUIModel(
    val url: String?,
    val title: String?,
    val link: String?,
)

data class PodcastRSSFeedItemUIModel(
    val title: String,
    val description: String,
    val link: String,
    val pubDate: String,
    val enclosure: PodcastRSSFeedEnclosureUIModel,
    val itunesDuration: String,
    val itunesSummary: String?,
)

data class PodcastRSSFeedEnclosureUIModel(
    val url: String,
    val length: String,
    val type: String,
)