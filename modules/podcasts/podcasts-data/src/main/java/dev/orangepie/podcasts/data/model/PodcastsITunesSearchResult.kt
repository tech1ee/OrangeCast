package dev.orangepie.podcasts.data.model

data class PodcastsITunesSearchResult(
    val resultCount: Int,
    val results: List<PodcastsITunesResponse>
)
