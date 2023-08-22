package dev.orangepie.data.repository

import dev.orangepie.data.model.PodcastLibraryRepoModel

interface PodcastLibraryRepository {

    suspend fun getAll(): List<PodcastLibraryRepoModel>

    suspend fun getPodcast(itunesId: Long): PodcastLibraryRepoModel?

    suspend fun save(podcast: PodcastLibraryRepoModel)

    suspend fun delete(podcast: PodcastLibraryRepoModel)
}