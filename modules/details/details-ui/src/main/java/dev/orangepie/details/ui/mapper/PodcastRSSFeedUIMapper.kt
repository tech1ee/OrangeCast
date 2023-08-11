package dev.orangepie.details.ui.mapper

import android.text.Html
import dev.orangepie.details.domain.model.PodcastRSSFeedItemModel
import dev.orangepie.details.domain.model.PodcastRSSFeedModel
import dev.orangepie.details.ui.model.PodcastRSSFeedItemUIModel
import dev.orangepie.details.ui.model.PodcastRSSFeedUIModel
import kotlinx.collections.immutable.toPersistentList
import javax.inject.Inject

class PodcastRSSFeedUIMapper @Inject constructor() {

    fun toUIModel(model: PodcastRSSFeedModel): PodcastRSSFeedUIModel {
        return PodcastRSSFeedUIModel(
            description = Html.fromHtml(model.description, Html.FROM_HTML_MODE_COMPACT).toString(),
            items = model.items.map { it.toUIModel() }.toPersistentList(),
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