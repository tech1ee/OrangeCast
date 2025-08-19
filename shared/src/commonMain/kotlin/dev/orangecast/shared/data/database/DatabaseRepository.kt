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

    suspend fun insertPodcast(podcast: PodcastEntity): Result<Unit> {
        return withContext(Dispatchers.Default) {
            try {
                database.transaction {
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
                        listenNotesId = podcast.listenNotesId,
                        genreIds = podcast.genreIds,
                        episodeCount = podcast.episodeCount,
                        isSubscribed = podcast.isSubscribed,
                        lastUpdated = podcast.lastUpdated,
                        createdAt = podcast.createdAt
                    )
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updatePodcastSubscription(id: String, isSubscribed: Boolean): Result<Unit> {
        return withContext(Dispatchers.Default) {
            try {
                database.transaction {
                    val timestamp = Clock.System.now().epochSeconds
                    podcastQueries.updatePodcastSubscription(
                        isSubscribed = if (isSubscribed) 1L else 0L,
                        lastUpdated = timestamp,
                        id = id
                    )
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun updatePodcastRssUrl(id: String, rssUrl: String): Result<Unit> {
        return withContext(Dispatchers.Default) {
            try {
                database.transaction {
                    val timestamp = Clock.System.now().epochSeconds
                    podcastQueries.updatePodcastRssUrl(
                        rssUrl = rssUrl,
                        lastUpdated = timestamp,
                        id = id
                    )
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deletePodcast(id: String): Result<Unit> {
        return withContext(Dispatchers.Default) {
            try {
                database.transaction {
                    podcastQueries.deletePodcast(id)
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updatePodcastWithListenNotesData(
        id: String,
        listenNotesId: String,
        genreIds: String,
        description: String,
        episodeCount: Long,
        lastUpdated: Long
    ): Result<Unit> {
        return withContext(Dispatchers.Default) {
            try {
                database.transaction {
                    podcastQueries.updatePodcastWithListenNotesData(
                        listenNotesId = listenNotesId,
                        genreIds = genreIds,
                        description = description,
                        episodeCount = episodeCount,
                        lastUpdated = lastUpdated,
                        id = id
                    )
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getPodcastByListenNotesId(listenNotesId: String): PodcastEntity? {
        return withContext(Dispatchers.Default) {
            podcastQueries.selectPodcastByListenNotesId(listenNotesId)
                .executeAsOneOrNull()
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

    suspend fun insertEpisode(episode: EpisodeEntity): Result<Unit> {
        return withContext(Dispatchers.Default) {
            try {
                database.transaction {
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
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateEpisodePlaybackState(
        id: String, 
        isPlayed: Boolean, 
        playbackPosition: Long
    ): Result<Unit> {
        return withContext(Dispatchers.Default) {
            try {
                database.transaction {
                    val timestamp = Clock.System.now().epochSeconds
                    episodeQueries.updateEpisodePlaybackState(
                        isPlayed = if (isPlayed) 1L else 0L,
                        playbackPosition = playbackPosition,
                        updatedAt = timestamp,
                        id = id
                    )
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteEpisode(id: String): Result<Unit> {
        return withContext(Dispatchers.Default) {
            try {
                database.transaction {
                    episodeQueries.deleteEpisode(id)
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Settings operations
    suspend fun getSetting(key: String): String? {
        return withContext(Dispatchers.Default) {
            settingsQueries.selectSetting(key)
                .executeAsOneOrNull()
        }
    }

    suspend fun setSetting(key: String, value: String): Result<Unit> {
        return withContext(Dispatchers.Default) {
            try {
                database.transaction {
                    val timestamp = Clock.System.now().epochSeconds
                    settingsQueries.insertOrUpdateSetting(
                        key,
                        value,
                        timestamp
                    )
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteSetting(key: String): Result<Unit> {
        return withContext(Dispatchers.Default) {
            try {
                database.transaction {
                    settingsQueries.deleteSetting(key)
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}