package dev.orangepie.details.data.model

data class PodcastDetailsRepoModel(
    val collectionId: Long,
    val trackId: Long,
    val artistName: String,
    val collectionName: String,
    val trackName: String,
    val collectionViewUrl: String,
    val feedUrl: String,
    val artworkUrl100: String,
    val genres: List<String>,
)
