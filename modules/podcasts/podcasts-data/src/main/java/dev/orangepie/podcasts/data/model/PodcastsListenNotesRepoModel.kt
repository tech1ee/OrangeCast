package dev.orangepie.podcasts.data.model

data class PodcastsListenNotesRepoModel(
    val podcasts: List<PodcastListenNotesRepoModel>?
)

data class PodcastListenNotesRepoModel(
    val id: String?,
    val title: String?,
    val publisher: String?,
    val image: String?,
    val thumbnail: String,
    val totalEpisodes: Int?,
    val explicitContent: Boolean?,
    val description: String?,
    val itunesId: Int?,
    val language: String?,
    val country: String?,
    val website: String?,
    val isClaimed: Boolean?,
    val genreIds: List<Int>?
)
