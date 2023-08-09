package dev.orangepie.details.ui.mapper

import dev.orangepie.details.domain.model.PodcastRSSFeedItemModel
import dev.orangepie.details.domain.model.PodcastRSSFeedModel
import dev.orangepie.details.ui.model.PodcastRSSFeedItemUIModel
import dev.orangepie.details.ui.model.PodcastRSSFeedUIModel
import javax.inject.Inject

class PodcastRSSFeedUIMapper @Inject constructor() {

    fun toUIModel(model: PodcastRSSFeedModel): PodcastRSSFeedUIModel {
        return PodcastRSSFeedUIModel(
            description = model.description,
            items = model.items.map { it.toUIModel() },
        )
    }

    private fun PodcastRSSFeedItemModel.toUIModel(): PodcastRSSFeedItemUIModel {
        return PodcastRSSFeedItemUIModel(
            title = title,
            description = description,
            link = link,
            pubDate = pubDate?.formatDate(),
            episode = episode,
            season = season,
            itunesDuration = itunesDuration,
            itunesSummary = itunesSummary,
        )
    }

    private fun String?.formatDate(): String? {
        return this?.split(", ")
            ?.get(1)
            ?.split(" ")
            ?.take(2)
            ?.joinToString(" ")
    }
}