package dev.orangepie.details.domain.model

data class PodcastDetailsModel(
    val collectionId: Long,
    val trackId: Long,
    val artistName: String,
    val collectionName: String,
    val trackName: String,
    val collectionViewUrl: String,
    val feedUrl: String,
    val artworkUrl100: String,
    val genres: List<String>,
    val feed: PodcastRSSFeedModel,
)
