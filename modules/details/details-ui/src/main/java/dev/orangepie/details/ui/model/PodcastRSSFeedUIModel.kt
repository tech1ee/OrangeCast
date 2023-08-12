package dev.orangepie.details.ui.model

import kotlinx.collections.immutable.ImmutableList

data class PodcastRSSFeedUIModel(
    val description: String?,
    val items: ImmutableList<PodcastRSSFeedItemUIModel>,
)

data class PodcastRSSFeedItemUIModel(
    val title: String?,
    val description: String?,
    val audio: String?,
    val link: String?,
    val pubDate: String?,
    val episode: String?,
    val season: String?,
    val itunesDuration: String?,
    val itunesSummary: String?,
    val state: PodcastRSSFeedItemUIState = PodcastRSSFeedItemUIState.None,
)

sealed class PodcastRSSFeedItemUIState {
    object None : PodcastRSSFeedItemUIState()
    object Loading : PodcastRSSFeedItemUIState()
    object Playing : PodcastRSSFeedItemUIState()
    object Paused : PodcastRSSFeedItemUIState()
}