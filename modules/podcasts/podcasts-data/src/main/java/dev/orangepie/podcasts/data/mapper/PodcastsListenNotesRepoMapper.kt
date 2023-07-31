package dev.orangepie.podcasts.data.mapper

import dev.orangepie.podcasts.data.model.PodcastListenNotesRepoModel
import dev.orangepie.podcasts.data.model.PodcastsBestListenNotesResponse
import dev.orangepie.podcasts.data.model.PodcastsListenNotesRepoModel

class PodcastsListenNotesRepoMapper {

    fun toRepoModel(response: PodcastsBestListenNotesResponse): PodcastsListenNotesRepoModel {
        return PodcastsListenNotesRepoModel(
            podcasts = response.podcasts?.map { podcast ->
                PodcastListenNotesRepoModel(
                    id = podcast.id,
                    title = podcast.title,
                    publisher = podcast.publisher,
                    image = podcast.image,
                    thumbnail = podcast.thumbnail,
                    totalEpisodes = podcast.totalEpisodes,
                    explicitContent = podcast.explicitContent,
                    description = podcast.description,
                    itunesId = podcast.itunesId,
                    language = podcast.language,
                    country = podcast.country,
                    website = podcast.website,
                    isClaimed = podcast.isClaimed,
                    genreIds = podcast.genreIds
                )
            }
        )
    }
}