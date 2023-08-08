package dev.orangepie.details.data.mapper

import dev.orangepie.details.data.model.PodcastRSSFeedEnclosureRepoModel
import dev.orangepie.details.data.model.PodcastRSSFeedItemRepoModel
import dev.orangepie.details.data.model.PodcastRSSFeedRepoModel
import dev.orangepie.details.data.model.PodcastRSSFeedResponse
import javax.inject.Inject

class PodcastRSSFeedRepoMapper @Inject constructor() {

    fun toRepoModel(response: PodcastRSSFeedResponse): PodcastRSSFeedRepoModel {
        return PodcastRSSFeedRepoModel(
            description = response.channel.description,
            items = response.channel.items.map { item ->
                PodcastRSSFeedItemRepoModel(
                    title = item.title,
                    description = item.description,
                    link = item.link,
                    pubDate = item.pubDate,
                    enclosure = PodcastRSSFeedEnclosureRepoModel(
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