package dev.orangecast.shared.domain.repository

import dev.orangecast.shared.domain.model.PlaybackPosition
import kotlinx.coroutines.flow.Flow

interface PlaybackPositionRepository {
    suspend fun savePosition(position: PlaybackPosition): Result<Unit>
    suspend fun getPosition(episodeId: String): PlaybackPosition?
    suspend fun clearPosition(episodeId: String): Result<Unit>
    suspend fun markAsCompleted(episodeId: String): Result<Unit>
    suspend fun getRecentPositions(limit: Int = 10): List<PlaybackPosition>
    fun observePosition(episodeId: String): Flow<PlaybackPosition?>
    suspend fun cleanupOldPositions(olderThanDays: Int = 30): Result<Unit>
}