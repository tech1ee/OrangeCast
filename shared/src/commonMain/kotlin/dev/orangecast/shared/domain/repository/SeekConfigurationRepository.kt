package dev.orangecast.shared.domain.repository

import dev.orangecast.shared.domain.model.SeekConfiguration
import kotlinx.coroutines.flow.Flow

interface SeekConfigurationRepository {
    suspend fun saveSeekConfiguration(config: SeekConfiguration)
    suspend fun getSeekConfiguration(): SeekConfiguration
    suspend fun saveForwardInterval(intervalMs: Long)
    suspend fun saveBackwardInterval(intervalMs: Long)
    suspend fun enableSmartRewind(enabled: Boolean)
    suspend fun enableHapticFeedback(enabled: Boolean)
    fun observeSeekConfiguration(): Flow<SeekConfiguration>
}