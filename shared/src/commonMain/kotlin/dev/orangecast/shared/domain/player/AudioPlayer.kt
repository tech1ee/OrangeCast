package dev.orangecast.shared.domain.player

import dev.orangecast.shared.domain.model.PodcastEpisode
import kotlinx.coroutines.flow.Flow

expect class AudioPlayer {
    fun play(episode: PodcastEpisode)
    fun pause()
    fun stop()
    fun seekTo(positionMs: Long)
    fun getCurrentPosition(): Long
    fun getDuration(): Long
    fun isPlaying(): Boolean
    val playerEvents: Flow<PlayerEvent>
    fun release()
}