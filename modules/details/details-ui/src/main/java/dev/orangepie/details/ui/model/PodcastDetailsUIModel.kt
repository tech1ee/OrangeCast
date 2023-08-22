package dev.orangepie.details.ui.model

data class PodcastDetailsUIModel(
    val collectionId: Long,
    val trackId: Long,
    val artistName: String,
    val collectionName: String,
    val trackName: String,
    val collectionViewUrl: String,
    val feedUrl: String,
    val artworkUrl100: String,
    val genres: List<String>,
    val feed: PodcastRSSFeedUIModel,
    val subscribed: Boolean = false,
)