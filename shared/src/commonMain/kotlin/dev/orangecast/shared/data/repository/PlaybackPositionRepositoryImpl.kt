package dev.orangecast.shared.data.repository

import dev.orangecast.shared.PodcastDatabase
import dev.orangecast.shared.domain.model.PlaybackPosition
import dev.orangecast.shared.domain.repository.PlaybackPositionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class PlaybackPositionRepositoryImpl(
    private val database: PodcastDatabase
) : PlaybackPositionRepository {
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val positionCache = mutableMapOf<String, MutableStateFlow<PlaybackPosition?>>()
    
    override suspend fun savePosition(position: PlaybackPosition): Result<Unit> {
        return try {
            val nowMillis = position.lastUpdated.toEpochMilliseconds()
            
            database.podcastDatabaseQueries.updateEpisodeProgress(
                playProgress = position.position,
                lastPlayedAt = nowMillis,
                id = position.episodeId
            )
            
            if (position.shouldMarkAsPlayed) {
                database.podcastDatabaseQueries.markEpisodeAsPlayed(
                    lastPlayedAt = nowMillis,
                    id = position.episodeId
                )
            }
            
            getOrCreateFlow(position.episodeId).value = position
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPosition(episodeId: String): PlaybackPosition? {
        return try {
            val episode = database.podcastDatabaseQueries.getEpisodeById(episodeId).executeAsOneOrNull()
            if (episode != null && episode.lastPlayedAt != null) {
                PlaybackPosition(
                    episodeId = episodeId,
                    position = episode.playProgress,
                    duration = episode.duration,
                    lastUpdated = Instant.fromEpochMilliseconds(episode.lastPlayedAt),
                    isCompleted = episode.isPlayed == 1L
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun clearPosition(episodeId: String): Result<Unit> {
        return try {
            database.podcastDatabaseQueries.markEpisodeAsUnplayed(episodeId)
            getOrCreateFlow(episodeId).value = null
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markAsCompleted(episodeId: String): Result<Unit> {
        return try {
            val nowMillis = Clock.System.now().toEpochMilliseconds()
            database.podcastDatabaseQueries.markEpisodeAsPlayed(
                lastPlayedAt = nowMillis,
                id = episodeId
            )
            
            val episode = database.podcastDatabaseQueries.getEpisodeById(episodeId).executeAsOneOrNull()
            if (episode != null) {
                val completedPosition = PlaybackPosition(
                    episodeId = episodeId,
                    position = episode.duration,
                    duration = episode.duration,
                    lastUpdated = Instant.fromEpochMilliseconds(nowMillis),
                    isCompleted = true
                )
                getOrCreateFlow(episodeId).value = completedPosition
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRecentPositions(limit: Int): List<PlaybackPosition> {
        return try {
            database.podcastDatabaseQueries.getRecentlyPlayedEpisodes(limit.toLong())
                .executeAsList()
                .mapNotNull { episode ->
                    if (episode.lastPlayedAt != null) {
                        PlaybackPosition(
                            episodeId = episode.id,
                            position = episode.playProgress,
                            duration = episode.duration,
                            lastUpdated = Instant.fromEpochMilliseconds(episode.lastPlayedAt),
                            isCompleted = episode.isPlayed == 1L
                        )
                    } else null
                }
        } catch (e: Exception) {
            throw e
        }
    }
    
    override fun observePosition(episodeId: String): Flow<PlaybackPosition?> {
        return getOrCreateFlow(episodeId).asStateFlow()
    }
    
    override suspend fun cleanupOldPositions(olderThanDays: Int): Result<Unit> {
        return try {
            val cutoffTime = Clock.System.now().minus(kotlin.time.Duration.parse("${olderThanDays}d"))
            database.podcastDatabaseQueries.clearOldPlayHistory(cutoffTime.toEpochMilliseconds())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun getOrCreateFlow(episodeId: String): MutableStateFlow<PlaybackPosition?> {
        return positionCache.getOrPut(episodeId) {
            val flow = MutableStateFlow<PlaybackPosition?>(null)
            
            scope.launch {
                flow.value = getPosition(episodeId)
            }
            
            flow
        }
    }
}