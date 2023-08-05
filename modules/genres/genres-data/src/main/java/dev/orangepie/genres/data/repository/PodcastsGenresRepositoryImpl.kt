package dev.orangepie.genres.data.repository

import dev.orangepie.genres.data.api.PodcastGenresListenNotesApi
import dev.orangepie.genres.data.mapper.PodcastGenreRepoMapper
import dev.orangepie.genres.data.model.PodcastGenreListenNotesRepoModel
import javax.inject.Inject

class PodcastsGenresRepositoryImpl @Inject constructor(
    private val podcastGenreRepoMapper: PodcastGenreRepoMapper,
    private val api: PodcastGenresListenNotesApi,
): PodcastsGenresRepository {

    private val genresListenNotes = mutableSetOf<PodcastGenreListenNotesRepoModel>()

    override suspend fun getPodcastGenresListenNotes(): List<PodcastGenreListenNotesRepoModel> {
        if (genresListenNotes.isEmpty()) {
            val response = api.getPodcastGenres()
            val genresRepoModels = response.genres
                .map { podcastGenreRepoMapper.toRepoModel(it) }
                .filter { !it.name.contains("Podcast") }
            genresListenNotes.addAll(genresRepoModels)
        }
        return genresListenNotes.toList()
    }
}