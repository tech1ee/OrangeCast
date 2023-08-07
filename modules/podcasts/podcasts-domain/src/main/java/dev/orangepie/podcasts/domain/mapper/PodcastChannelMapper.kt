package dev.orangepie.podcasts.domain.mapper

import dev.orangepie.genres.domain.model.PodcastGenreModel
import dev.orangepie.podcasts.data.model.PodcastListenNotesRepoModel
import dev.orangepie.podcasts.data.model.PodcastsITunesRepoModel
import dev.orangepie.podcasts.domain.model.PodcastChannelModel
import javax.inject.Inject

class PodcastChannelMapper @Inject constructor() {

    fun toModel(repoModel: PodcastsITunesRepoModel): PodcastChannelModel {
        return PodcastChannelModel(
            itunesId = repoModel.artistId,
            title = repoModel.artistName,
            imagePreviewUrl = repoModel.artworkUrl100,
            imageFullUrl = repoModel.artworkUrl600,
            genres = listOf(
                PodcastGenreModel(
                    id = repoModel.genreIds?.firstOrNull()?.toInt() ?: 0,
                    name = repoModel.genres?.firstOrNull() ?: "",
                    parentId = null,
                )
            )
        )
    }

    fun toModel(
        repoModel: PodcastListenNotesRepoModel,
        genres: List<PodcastGenreModel>
    ): PodcastChannelModel {
        return PodcastChannelModel(
            itunesId = repoModel.itunesId?.toLong(),
            title = repoModel.title,
            imagePreviewUrl = repoModel.thumbnail,
            imageFullUrl = repoModel.image,
            genres = repoModel.genreIds?.mapNotNull { id ->
                genres.firstOrNull { it.id == id }
            } ?: emptyList(),
        )
    }
}