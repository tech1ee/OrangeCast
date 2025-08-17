package dev.orangecast.shared.data.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import dev.orangecast.shared.database.EpisodeEntity
import dev.orangecast.shared.database.OrangeCastDatabase
import dev.orangecast.shared.database.PodcastEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class DatabaseRepository(
    private val database: OrangeCastDatabase
) {
    private val podcastQueries = database.podcastQueries
    private val episodeQueries = database.episodeQueries
    private val settingsQueries = database.userSettingsQueries

    // Podcast operations
    fun getAllPodcasts(): Flow<List<PodcastEntity>> {
        return podcastQueries.selectAllPodcasts()
            .asFlow()
            .mapToList(Dispatchers.Default)
    }

    fun getSubscribedPodcasts(): Flow<List<PodcastEntity>> {
        return podcastQueries.selectSubscribedPodcasts()
            .asFlow()
            .mapToList(Dispatchers.Default)
    }

    suspend fun getPodcastById(id: String): PodcastEntity? {
        return withContext(Dispatchers.Default) {
            podcastQueries.selectPodcastById(id)
                .executeAsOneOrNull()
        }
    }

    suspend fun insertPodcast(podcast: PodcastEntity) {
        withContext(Dispatchers.Default) {
            podcastQueries.insertPodcast(
                id = podcast.id,
                title = podcast.title,
                author = podcast.author,
                description = podcast.description,
                imageUrl = podcast.imageUrl,
                rssUrl = podcast.rssUrl,
                websiteUrl = podcast.websiteUrl,
                language = podcast.language,
                genres = podcast.genres,
                isSubscribed = podcast.isSubscribed,
                lastUpdated = podcast.lastUpdated,
                createdAt = podcast.createdAt
            )
        }
    }

    suspend fun updatePodcastSubscription(id: String, isSubscribed: Boolean) {
        withContext(Dispatchers.Default) {
            val timestamp = Clock.System.now().epochSeconds
            podcastQueries.updatePodcastSubscription(
                isSubscribed = if (isSubscribed) 1L else 0L,
                lastUpdated = timestamp,
                id = id
            )
        }
    }

    suspend fun deletePodcast(id: String) {
        withContext(Dispatchers.Default) {
            podcastQueries.deletePodcast(id)
        }
    }

    // Episode operations
    fun getEpisodesByPodcast(podcastId: String): Flow<List<EpisodeEntity>> {
        return episodeQueries.selectEpisodesByPodcast(podcastId)
            .asFlow()
            .mapToList(Dispatchers.Default)
    }

    fun getRecentEpisodes(limit: Long = 50): Flow<List<dev.orangecast.shared.database.SelectRecentEpisodes>> {
        return episodeQueries.selectRecentEpisodes(limit)
            .asFlow()
            .mapToList(Dispatchers.Default)
    }

    fun getUnplayedEpisodes(): Flow<List<EpisodeEntity>> {
        return episodeQueries.selectUnplayedEpisodes()
            .asFlow()
            .mapToList(Dispatchers.Default)
    }
    
    fun getNewEpisodesFromSubscribedPodcasts(limit: Long = 50): Flow<List<dev.orangecast.shared.database.SelectNewEpisodesFromSubscribedPodcasts>> {
        return episodeQueries.selectNewEpisodesFromSubscribedPodcasts(limit)
            .asFlow()
            .mapToList(Dispatchers.Default)
    }

    suspend fun getEpisodeById(id: String): EpisodeEntity? {
        return withContext(Dispatchers.Default) {
            episodeQueries.selectEpisodeById(id)
                .executeAsOneOrNull()
        }
    }

    suspend fun insertEpisode(episode: EpisodeEntity) {
        withContext(Dispatchers.Default) {
            try {
                episodeQueries.insertEpisode(
                    id = episode.id,
                    podcastId = episode.podcastId,
                    title = episode.title,
                    description = episode.description,
                    audioUrl = episode.audioUrl,
                    duration = episode.duration,
                    publishedAt = episode.publishedAt,
                    episodeNumber = episode.episodeNumber,
                    seasonNumber = episode.seasonNumber,
                    episodeType = episode.episodeType,
                    isPlayed = episode.isPlayed,
                    playbackPosition = episode.playbackPosition,
                    isDownloaded = episode.isDownloaded,
                    downloadPath = episode.downloadPath,
                    fileSize = episode.fileSize,
                    createdAt = episode.createdAt,
                    updatedAt = episode.updatedAt
                )
            } catch (e: Exception) {
                // Handle database constraint violations or other insertion errors
            }
        }
    }

    suspend fun updateEpisodePlaybackState(
        id: String, 
        isPlayed: Boolean, 
        playbackPosition: Long
    ) {
        withContext(Dispatchers.Default) {
            val timestamp = Clock.System.now().epochSeconds
            episodeQueries.updateEpisodePlaybackState(
                isPlayed = if (isPlayed) 1L else 0L,
                playbackPosition = playbackPosition,
                updatedAt = timestamp,
                id = id
            )
        }
    }

    suspend fun deleteEpisode(id: String) {
        withContext(Dispatchers.Default) {
            episodeQueries.deleteEpisode(id)
        }
    }

    // Settings operations
    suspend fun getSetting(key: String): String? {
        return withContext(Dispatchers.Default) {
            settingsQueries.selectSetting(key)
                .executeAsOneOrNull()
        }
    }

    suspend fun setSetting(key: String, value: String) {
        withContext(Dispatchers.Default) {
            val timestamp = Clock.System.now().epochSeconds
            settingsQueries.insertOrUpdateSetting(
                key,
                value,
                timestamp
            )
        }
    }

    suspend fun deleteSetting(key: String) {
        withContext(Dispatchers.Default) {
            settingsQueries.deleteSetting(key)
        }
    }
}