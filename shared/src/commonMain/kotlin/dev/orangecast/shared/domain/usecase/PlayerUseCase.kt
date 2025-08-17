package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.PodcastEpisode
import dev.orangecast.shared.domain.player.AudioPlayer
import dev.orangecast.shared.domain.player.PlayerEvent
import kotlinx.coroutines.flow.Flow

class PlayerUseCase(
    private val audioPlayer: AudioPlayer
) {
    val playerEvents: Flow<PlayerEvent> = audioPlayer.playerEvents

    fun playEpisode(episode: PodcastEpisode) {
        audioPlayer.play(episode)
    }

    fun pausePlayback() {
        audioPlayer.pause()
    }

    fun stopPlayback() {
        audioPlayer.stop()
    }

    fun seekTo(positionMs: Long) {
        audioPlayer.seekTo(positionMs)
    }

    fun getCurrentPosition(): Long = audioPlayer.getCurrentPosition()

    fun getDuration(): Long = audioPlayer.getDuration()

    fun isPlaying(): Boolean = audioPlayer.isPlaying()

    fun release() {
        audioPlayer.release()
    }
}