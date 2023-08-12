package dev.orangepie.player.domain.mapper

import androidx.media3.common.MediaItem
import dev.orangepie.details.domain.model.PodcastRSSFeedItemModel
import javax.inject.Inject

class PlayerMapper @Inject constructor() {

    fun toMediaItem(podcast: PodcastRSSFeedItemModel): MediaItem {
        return MediaItem.Builder()
            .setUri(podcast.audio)
            .build()
    }
}