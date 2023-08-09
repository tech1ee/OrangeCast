package dev.orangepie.details.data.mapper

import dev.orangepie.details.data.model.PodcastDetailsITunesResponse
import dev.orangepie.details.data.model.PodcastDetailsRepoModel
import javax.inject.Inject

class PodcastDetailsRepoMapper @Inject constructor() {
    fun toRepoModel(response: PodcastDetailsITunesResponse): PodcastDetailsRepoModel {
         return response.results.first().let { item ->
            PodcastDetailsRepoModel(
                collectionId = item.collectionId,
                trackId = item.trackId,
                artistName = item.artistName,
                collectionName = item.collectionName,
                trackName = item.trackName,
                collectionViewUrl = item.collectionViewUrl,
                feedUrl = item.feedUrl,
                artworkUrl100 = item.artworkUrl100,
                genres = item.genres,
            )
        }
    }
}