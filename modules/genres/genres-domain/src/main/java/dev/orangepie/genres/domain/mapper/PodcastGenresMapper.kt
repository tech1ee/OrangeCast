package dev.orangepie.genres.domain.mapper

import dev.orangepie.genres.data.model.PodcastGenreListenNotesRepoModel
import dev.orangepie.genres.domain.model.PodcastGenreModel
import javax.inject.Inject

class PodcastGenresMapper @Inject constructor() {

    suspend fun toModel(repoModel: PodcastGenreListenNotesRepoModel): PodcastGenreModel {
        return PodcastGenreModel(
            id = repoModel.id,
            name = repoModel.name,
            parentId = repoModel.parentId,
        )
    }
}