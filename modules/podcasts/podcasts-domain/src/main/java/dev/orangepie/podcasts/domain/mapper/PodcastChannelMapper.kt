package dev.orangepie.podcasts.domain.mapper

import dev.orangepie.podcasts.data.model.PodcastListenNotesRepoModel
import dev.orangepie.podcasts.data.model.PodcastsITunesRepoModel
import dev.orangepie.podcasts.domain.model.PodcastChannelModel

class PodcastChannelMapper {

    fun toModel(repoModel: PodcastsITunesRepoModel): PodcastChannelModel {
        return PodcastChannelModel(
            itunesId = repoModel.artistId,
            title = repoModel.artistName,
            imagePreviewUrl = repoModel.artworkUrl100,
            imageFullUrl = repoModel.artworkUrl600,
            genre = repoModel.primaryGenreName
        )
    }

    fun toModel(repoModel: PodcastListenNotesRepoModel): PodcastChannelModel {
        return PodcastChannelModel(
            itunesId = repoModel.itunesId?.toLong(),
            title = repoModel.title,
            imagePreviewUrl = repoModel.thumbnail,
            imageFullUrl = repoModel.image,
            genre = repoModel.genreIds?.firstOrNull()?.toString()
        )
    }
}