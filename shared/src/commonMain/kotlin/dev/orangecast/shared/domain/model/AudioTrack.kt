package dev.orangecast.shared.domain.model

data class AudioTrack(
    val id: String,
    val title: String,
    val artist: String,
    val albumTitle: String,
    val duration: Long,
    val url: String,
    val imageUrl: String? = null,
    val mimeType: String = "audio/mpeg"
) {
    companion object {
        fun fromPodcastEpisode(
            episodeId: String,
            episodeTitle: String,
            podcastTitle: String,
            episodeUrl: String,
            duration: Long = 0,
            artworkUrl: String? = null
        ): AudioTrack {
            return AudioTrack(
                id = episodeId,
                title = episodeTitle,
                artist = podcastTitle,
                albumTitle = podcastTitle,
                duration = duration,
                url = episodeUrl,
                imageUrl = artworkUrl
            )
        }
    }
}