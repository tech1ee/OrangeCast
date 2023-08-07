package dev.orangepie.genres.data.mapper

import dev.orangepie.genres.data.model.PodcastGenreListenNotesRepoModel
import dev.orangepie.genres.data.model.PodcastGenreListenNotesResponse
import javax.inject.Inject

class PodcastGenreRepoMapper @Inject constructor() {

        fun toRepoModel(response: PodcastGenreListenNotesResponse): PodcastGenreListenNotesRepoModel {
            return PodcastGenreListenNotesRepoModel(
                id = response.id,
                name = response.name,
                parentId = response.parentId,
            )
        }
}