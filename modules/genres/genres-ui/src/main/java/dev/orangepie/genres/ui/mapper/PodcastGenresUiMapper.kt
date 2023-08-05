package dev.orangepie.genres.ui.mapper

import dev.orangepie.genres.domain.model.PodcastGenreModel
import dev.orangepie.genres.ui.model.PodcastGenreUiModel
import javax.inject.Inject

class PodcastGenresUiMapper @Inject constructor() {

    fun toUIModel(model: PodcastGenreModel): PodcastGenreUiModel {
        return PodcastGenreUiModel(
            id = model.id,
            name = model.name,
            parentId = model.parentId,
        )
    }
}