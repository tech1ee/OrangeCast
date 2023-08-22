package dev.orangepie.library.domain.mapper

import dev.orangepie.data.model.PodcastLibraryRepoModel
import dev.orangepie.details.domain.model.PodcastDetailsModel
import dev.orangepie.podcasts.domain.model.PodcastChannelModel
import javax.inject.Inject

class PodcastLibraryMapper @Inject constructor() {

    fun toModel(repoModel: PodcastLibraryRepoModel): PodcastChannelModel {
        return PodcastChannelModel(
            itunesId = repoModel.itunesId,
            title = repoModel.title,
            imagePreviewUrl = repoModel.imagePreviewUrl,
            imageFullUrl = repoModel.imageFullUrl,
        )
    }

    fun toRepoModel(model: PodcastDetailsModel): PodcastLibraryRepoModel {
        return PodcastLibraryRepoModel(
            itunesId = model.collectionId,
            title = model.collectionName,
            imagePreviewUrl = model.artworkUrl100,
            imageFullUrl = model.artworkUrl100,
        )
    }
}