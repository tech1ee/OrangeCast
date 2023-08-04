package dev.orangepie.genres.data.repository

import dev.orangepie.genres.data.model.PodcastGenreListenNotesRepoModel

interface PodcastsGenresRepository {

    suspend fun getPodcastGenresListenNotes(): List<PodcastGenreListenNotesRepoModel>
}