package dev.orangepie.details.data.mapper

import com.prof.rssparser.Channel
import dev.orangepie.details.data.model.PodcastRSSFeedItemRepoModel
import dev.orangepie.details.data.model.PodcastRSSFeedRepoModel
import javax.inject.Inject

class PodcastRSSFeedRepoMapper @Inject constructor() {

    fun toRepoModel(response: Channel): PodcastRSSFeedRepoModel {
        return PodcastRSSFeedRepoModel(
            description = response.description,
            items = response.articles.map { item ->
                PodcastRSSFeedItemRepoModel(
                    title = item.title,
                    description = item.description,
                    audio = item.audio,
                    link = item.link,
                    pubDate = item.pubDate,
                    episode = item.itunesArticleData?.episode,
                    season = item.itunesArticleData?.season,
                    itunesDuration = item.itunesArticleData?.duration,
                    itunesSummary = item.itunesArticleData?.summary,
                )
            },
        )
    }
}