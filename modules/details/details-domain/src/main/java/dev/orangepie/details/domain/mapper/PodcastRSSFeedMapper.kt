package dev.orangepie.details.domain.mapper

import dev.orangepie.details.data.model.PodcastRSSFeedRepoModel
import dev.orangepie.details.domain.model.PodcastRSSFeedEnclosureModel
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
                    link = item.link,
                    pubDate = item.pubDate,
                    enclosure = PodcastRSSFeedEnclosureModel(
                        url = item.enclosure.url,
                        length = item.enclosure.length,
                        type = item.enclosure.type,
                    ),
                    itunesDuration = item.itunesDuration,
                    itunesSummary = item.itunesSummary,
                )
            },
        )
    }
}