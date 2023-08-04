package dev.orangepie.genres.data.repository

import dev.orangepie.genres.data.api.PodcastGenresListenNotesApi
import dev.orangepie.genres.data.mapper.PodcastGenreRepoMapper
import dev.orangepie.genres.data.model.PodcastGenreListenNotesRepoModel
import javax.inject.Inject

class PodcastsGenresRepositoryImpl @Inject constructor(
    private val podcastGenreRepoMapper: PodcastGenreRepoMapper,
    private val api: PodcastGenresListenNotesApi,
): PodcastsGenresRepository {

    override suspend fun getPodcastGenresListenNotes(): List<PodcastGenreListenNotesRepoModel> {
        return api.getPodcastGenres().genres.map { podcastGenreRepoMapper.toRepoModel(it) }
    }
}