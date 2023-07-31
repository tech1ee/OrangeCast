package dev.orangepie.podcasts.data.repository

import dev.orangepie.podcasts.data.api.PodcastsITunesApi
import dev.orangepie.podcasts.data.api.PodcastsListenNotesApi
import dev.orangepie.podcasts.data.mapper.PodcastsITunesRepoMapper
import dev.orangepie.podcasts.data.model.PodcastListenNotesRepoModel
import dev.orangepie.podcasts.data.model.PodcastsITunesRepoModel
import dev.orangepie.podcasts.data.model.PodcastsListenNotesRepoModel
import javax.inject.Inject

class PodcastsRepositoryImpl @Inject constructor(
    private val itunesApi: PodcastsITunesApi,
    private val listenNotesApi: PodcastsListenNotesApi,
    private val podcastsMapper: PodcastsITunesRepoMapper
): PodcastsRepository {

    override suspend fun search(queryMap: Map<String, String>): List<PodcastsITunesRepoModel> {
        return itunesApi.search(queryMap).results
            .map { podcastsMapper.toRepoModel(it) }
    }

    override suspend fun getBestPodcasts(queryMap: Map<String, String>): List<PodcastListenNotesRepoModel> {
        return listenNotesApi.getBestPodcasts(queryMap).podcasts
            .map { podcastsMapper.toRepoModel(it) }
    }
}