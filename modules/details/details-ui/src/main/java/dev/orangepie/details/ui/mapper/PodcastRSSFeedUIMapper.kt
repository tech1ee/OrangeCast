package dev.orangepie.details.ui.mapper

import android.text.Html
import dev.orangepie.details.domain.model.PodcastRSSFeedItemModel
import dev.orangepie.details.domain.model.PodcastRSSFeedItemState
import dev.orangepie.details.domain.model.PodcastRSSFeedModel
import dev.orangepie.details.ui.model.PodcastRSSFeedItemUIModel
import dev.orangepie.details.ui.model.PodcastRSSFeedItemUIState
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

    fun toModel(uiModel: PodcastRSSFeedItemUIModel): PodcastRSSFeedItemModel {
        return PodcastRSSFeedItemModel(
            title = uiModel.title,
            description = uiModel.description,
            audio = uiModel.audio,
            link = uiModel.link,
            pubDate = uiModel.pubDate,
            episode = uiModel.episode,
            season = uiModel.season,
            itunesDuration = uiModel.itunesDuration,
            itunesSummary = uiModel.itunesSummary,
            state = uiModel.state.toState(),
        )
    }

    private fun PodcastRSSFeedItemModel.toUIModel(): PodcastRSSFeedItemUIModel {
        return PodcastRSSFeedItemUIModel(
            title = title,
            description = description,
            audio = audio,
            link = link,
            pubDate = pubDate?.formatDate(),
            episode = episode,
            season = season,
            itunesDuration = itunesDuration,
            itunesSummary = itunesSummary,
            state = state.toUIState(),
        )
    }

    private fun String?.formatDate(): String? {
        return this?.split(", ")
            ?.get(1)
            ?.split(" ")
            ?.take(2)
            ?.joinToString(" ")
    }

    private fun PodcastRSSFeedItemUIState.toState(): PodcastRSSFeedItemState {
        return when (this) {
            PodcastRSSFeedItemUIState.None -> PodcastRSSFeedItemState.None
            PodcastRSSFeedItemUIState.Loading -> PodcastRSSFeedItemState.Loading
            PodcastRSSFeedItemUIState.Playing -> PodcastRSSFeedItemState.Playing
            PodcastRSSFeedItemUIState.Paused -> PodcastRSSFeedItemState.Paused
        }
    }

    private fun PodcastRSSFeedItemState.toUIState(): PodcastRSSFeedItemUIState {
        return when (this) {
            PodcastRSSFeedItemState.None -> PodcastRSSFeedItemUIState.None
            PodcastRSSFeedItemState.Loading -> PodcastRSSFeedItemUIState.Loading
            PodcastRSSFeedItemState.Playing -> PodcastRSSFeedItemUIState.Playing
            PodcastRSSFeedItemState.Paused -> PodcastRSSFeedItemUIState.Paused
        }
    }
}