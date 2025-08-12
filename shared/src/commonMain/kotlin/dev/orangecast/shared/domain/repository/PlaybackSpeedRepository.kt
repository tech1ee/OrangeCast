package dev.orangecast.shared.domain.repository

import dev.orangecast.shared.domain.model.PlaybackSpeed
import kotlinx.coroutines.flow.Flow

interface PlaybackSpeedRepository {
    suspend fun savePreferredSpeed(podcastId: String, speed: PlaybackSpeed)
    suspend fun getPreferredSpeed(podcastId: String): PlaybackSpeed
    suspend fun getGlobalPreferredSpeed(): PlaybackSpeed
    suspend fun saveGlobalPreferredSpeed(speed: PlaybackSpeed)
    fun observeSpeedPreferences(): Flow<Map<String, PlaybackSpeed>>
}