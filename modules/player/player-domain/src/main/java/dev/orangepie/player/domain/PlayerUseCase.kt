package dev.orangepie.player.domain

import androidx.media3.common.Player
import dev.orangepie.details.domain.model.PodcastRSSFeedItemModel
import dev.orangepie.player.data.PlayerInstance
import dev.orangepie.player.domain.mapper.PlayerMapper
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerUseCase @Inject constructor(
    playerInstance: PlayerInstance,
    private val playerMapper: PlayerMapper,
) {
    val playerEvents = MutableSharedFlow<PlayerEvent>()

    private var currentPodcast: PodcastRSSFeedItemModel? = null

    private val playerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            Timber.i("Player events: $events")
            super.onEvents(player, events)
        }
        override fun onPlaybackStateChanged(state: Int) {
            Timber.i("Player state changed - state: $state")
            super.onPlaybackStateChanged(state)
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            if (isLoading) {
                emitEvent(PlayerEvent.Loading(podcast = currentPodcast))
            }
            Timber.i("Player loading changed - isLoading: $isLoading")
            super.onIsLoadingChanged(isLoading)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                emitEvent(PlayerEvent.Playing(podcast = currentPodcast))
            } else {
                emitEvent(PlayerEvent.Paused(podcast = currentPodcast))
            }
            Timber.i("Player playing changed - isPlaying: $isPlaying")
            super.onIsPlayingChanged(isPlaying)
        }
    }

    private val player: Player = playerInstance.initPlayer().apply {
        addListener(playerListener)
    }

    fun play(podcast: PodcastRSSFeedItemModel) {
        if (currentPodcast?.audio == podcast.audio) {
            player.play()
        } else {
            currentPodcast = podcast
            player.setMediaItem(playerMapper.toMediaItem(podcast))
            player.prepare()
            player.play()
        }
    }

    fun pause() {
        player.pause()
    }

    private fun emitEvent(event: PlayerEvent) {
        MainScope().launch {
            playerEvents.emit(event)
        }
    }
}