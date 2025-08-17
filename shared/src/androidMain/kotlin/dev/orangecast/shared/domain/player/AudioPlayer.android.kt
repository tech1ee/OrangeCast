package dev.orangecast.shared.domain.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dev.orangecast.shared.domain.model.PodcastEpisode
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

actual class AudioPlayer(
    private val context: Context
) {
    private var exoPlayer: ExoPlayer? = null
    private var currentEpisode: PodcastEpisode? = null

    private fun getPlayer(): ExoPlayer {
        return exoPlayer ?: ExoPlayer.Builder(context).build().also { 
            exoPlayer = it 
        }
    }

    actual fun play(episode: PodcastEpisode) {
        val player = getPlayer()
        
        if (currentEpisode?.audioUrl != episode.audioUrl) {
            currentEpisode = episode
            val mediaItem = MediaItem.fromUri(episode.audioUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
        }
        
        player.play()
    }

    actual fun pause() {
        exoPlayer?.pause()
    }

    actual fun stop() {
        exoPlayer?.stop()
        currentEpisode = null
    }

    actual fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
    }

    actual fun getCurrentPosition(): Long {
        return exoPlayer?.currentPosition ?: 0L
    }

    actual fun getDuration(): Long {
        return exoPlayer?.duration ?: 0L
    }

    actual fun isPlaying(): Boolean {
        return exoPlayer?.isPlaying ?: false
    }

    actual val playerEvents: Flow<PlayerEvent> = callbackFlow {
        val player = getPlayer()
        
        val listener = object : Player.Listener {
            override fun onIsLoadingChanged(isLoading: Boolean) {
                if (isLoading) {
                    trySend(PlayerEvent.Loading(currentEpisode))
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    trySend(PlayerEvent.Playing(currentEpisode))
                } else {
                    trySend(PlayerEvent.Paused(currentEpisode))
                }
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                trySend(PlayerEvent.Error(currentEpisode, error.message ?: "Unknown error"))
            }
        }
        
        player.addListener(listener)
        
        awaitClose {
            player.removeListener(listener)
        }
    }

    actual fun release() {
        exoPlayer?.release()
        exoPlayer = null
        currentEpisode = null
    }
}