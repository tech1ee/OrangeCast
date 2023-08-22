package dev.orangepie.data.mapper

import dev.orangepie.data.db.entity.PodcastLibraryEntity
import dev.orangepie.data.model.PodcastLibraryRepoModel
import javax.inject.Inject

class PodcastLibraryRepoMapper @Inject constructor() {

    fun toRepoModel(entity: PodcastLibraryEntity): PodcastLibraryRepoModel {
        return PodcastLibraryRepoModel(
            itunesId = entity.itunesId,
            title = entity.title,
            imagePreviewUrl = entity.imagePreviewUrl,
            imageFullUrl = entity.imageFullUrl,
        )
    }

    fun toEntity(model: PodcastLibraryRepoModel): PodcastLibraryEntity {
        return PodcastLibraryEntity(
            itunesId = model.itunesId,
            title = model.title,
            imagePreviewUrl = model.imagePreviewUrl,
            imageFullUrl = model.imageFullUrl,
        )
    }
}