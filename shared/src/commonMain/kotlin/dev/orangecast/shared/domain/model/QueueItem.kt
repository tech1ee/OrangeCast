package dev.orangecast.shared.domain.model

import kotlinx.datetime.Instant

data class QueueItem(
    val id: String,
    val episode: PodcastEpisode,
    val addedAt: Instant,
    val position: Long = 0L
) {
    companion object {
        fun fromEpisode(episode: PodcastEpisode): QueueItem {
            return QueueItem(
                id = "${episode.id}_${kotlinx.datetime.Clock.System.now().toEpochMilliseconds()}",
                episode = episode,
                addedAt = kotlinx.datetime.Clock.System.now()
            )
        }
        
        fun fromEpisodes(episodes: List<PodcastEpisode>): List<QueueItem> {
            return episodes.map { fromEpisode(it) }
        }
    }
}