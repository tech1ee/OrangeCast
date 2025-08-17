package dev.orangecast.shared.domain.player

import dev.orangecast.shared.domain.model.PodcastEpisode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import platform.AVFoundation.*
import platform.Foundation.*
import platform.CoreMedia.*
import kotlinx.cinterop.*

actual class AudioPlayer {
    private var avPlayer: AVPlayer? = null
    private var currentEpisode: PodcastEpisode? = null

    private fun getPlayer(): AVPlayer {
        return avPlayer ?: AVPlayer().also { avPlayer = it }
    }

    actual fun play(episode: PodcastEpisode) {
        val player = getPlayer()
        
        if (currentEpisode?.audioUrl != episode.audioUrl) {
            currentEpisode = episode
            val url = NSURL.URLWithString(episode.audioUrl)
            val playerItem = url?.let { AVPlayerItem.playerItemWithURL(it) }
            playerItem?.let { player.replaceCurrentItemWithPlayerItem(it) }
        }
        
        player.play()
    }

    actual fun pause() {
        avPlayer?.pause()
    }

    actual fun stop() {
        avPlayer?.pause()
        avPlayer?.replaceCurrentItemWithPlayerItem(null)
        currentEpisode = null
    }

    actual fun seekTo(positionMs: Long) {
        val timeValue = positionMs / 1000.0
        val cmTime = CMTimeMakeWithSeconds(timeValue, 1000000)
        avPlayer?.seekToTime(cmTime)
    }

    actual fun getCurrentPosition(): Long {
        val currentTime = avPlayer?.currentTime() ?: return 0L
        val seconds = CMTimeGetSeconds(currentTime)
        return if (seconds.isNaN()) 0L else (seconds * 1000).toLong()
    }

    actual fun getDuration(): Long {
        val currentItem = avPlayer?.currentItem ?: return 0L
        val duration = currentItem.duration
        val seconds = CMTimeGetSeconds(duration)
        return if (seconds.isNaN()) 0L else (seconds * 1000).toLong()
    }

    actual fun isPlaying(): Boolean {
        return avPlayer?.rate != 0.0
    }

    actual val playerEvents: Flow<PlayerEvent> = flowOf()

    actual fun release() {
        avPlayer?.pause()
        avPlayer = null
        currentEpisode = null
    }
}