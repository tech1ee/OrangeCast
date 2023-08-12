package dev.orangepie.player.domain

import dev.orangepie.details.domain.model.PodcastRSSFeedItemModel

sealed class PlayerEvent(open val podcast: PodcastRSSFeedItemModel? = null) {
    data class Loading(override val podcast: PodcastRSSFeedItemModel?) : PlayerEvent()
    data class Playing(override val podcast: PodcastRSSFeedItemModel?) : PlayerEvent()
    data class Paused(override val podcast: PodcastRSSFeedItemModel?) : PlayerEvent()
}
