package dev.orangepie.details.ui.model

import kotlinx.collections.immutable.ImmutableList

data class PodcastRSSFeedUIModel(
    val description: String?,
    val items: ImmutableList<PodcastRSSFeedItemUIModel>,
)

data class PodcastRSSFeedItemUIModel(
    val title: String?,
    val description: String?,
    val link: String?,
    val pubDate: String?,
    val episode: String?,
    val season: String?,
    val itunesDuration: String?,
    val itunesSummary: String?,
)