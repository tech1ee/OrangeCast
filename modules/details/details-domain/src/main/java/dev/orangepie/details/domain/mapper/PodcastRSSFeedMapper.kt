package dev.orangepie.details.domain.mapper

import dev.orangepie.details.data.model.PodcastRSSFeedRepoModel
import dev.orangepie.details.domain.model.PodcastRSSFeedItemModel
import dev.orangepie.details.domain.model.PodcastRSSFeedModel
import javax.inject.Inject

class PodcastRSSFeedMapper @Inject constructor() {

    fun toModel(repoModel: PodcastRSSFeedRepoModel): PodcastRSSFeedModel {
        return PodcastRSSFeedModel(
            description = repoModel.description,
            items = repoModel.items.map { item ->
                PodcastRSSFeedItemModel(
                    title = item.title,
                    description = item.description,
                    audio = item.audio,
                    link = item.link,
                    pubDate = item.pubDate,
                    episode = item.episode,
                    season = item.season,
                    itunesDuration = item.itunesDuration,
                    itunesSummary = item.itunesSummary,
                )
            },
        )
    }
}