package dev.orangepie.details.domain.mapper

import dev.orangepie.details.data.model.PodcastDetailsRepoModel
import dev.orangepie.details.domain.model.PodcastDetailsModel
import javax.inject.Inject

class PodcastDetailsMapper @Inject constructor() {

    fun toModel(repoModel: PodcastDetailsRepoModel): PodcastDetailsModel {
        return PodcastDetailsModel(
            collectionId = repoModel.collectionId,
            trackId = repoModel.trackId,
            artistName = repoModel.artistName,
            collectionName = repoModel.collectionName,
            trackName = repoModel.trackName,
            collectionViewUrl = repoModel.collectionViewUrl,
            feedUrl = repoModel.feedUrl,
            artworkUrl100 = repoModel.artworkUrl100,
            genres = repoModel.genres,
        )
    }
}