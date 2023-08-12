package dev.orangepie.player.domain

import androidx.media3.common.Player
import dev.orangepie.details.domain.model.PodcastRSSFeedItemModel
import dev.orangepie.player.data.PlayerInstance
import dev.orangepie.player.domain.mapper.PlayerMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerUseCase @Inject constructor(
    playerInstance: PlayerInstance,
    private val playerMapper: PlayerMapper,
) {

    private var player: Player = playerInstance.initPlayer()
    private var currentPodcast: PodcastRSSFeedItemModel? = null

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
}