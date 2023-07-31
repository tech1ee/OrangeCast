package dev.orangepie.podcasts.data.repository

import dev.orangepie.podcasts.data.model.PodcastListenNotesRepoModel
import dev.orangepie.podcasts.data.model.PodcastsITunesRepoModel

interface PodcastsRepository {

    suspend fun search(
        queryMap: Map<String, String>
    ): List<PodcastsITunesRepoModel>

    suspend fun getBestPodcasts(
        queryMap: Map<String, String>
    ): List<PodcastListenNotesRepoModel>
}