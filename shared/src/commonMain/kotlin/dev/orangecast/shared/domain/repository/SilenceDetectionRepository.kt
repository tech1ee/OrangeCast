package dev.orangecast.shared.domain.repository

import dev.orangecast.shared.domain.model.AudioAnalysisResult
import dev.orangecast.shared.domain.model.SilenceDetectionConfig
import dev.orangecast.shared.domain.model.SilenceAggressiveness
import kotlinx.coroutines.flow.Flow

interface SilenceDetectionRepository {
    suspend fun saveSkipSilencePreference(podcastId: String, enabled: Boolean)
    suspend fun getSkipSilencePreference(podcastId: String): Boolean
    suspend fun saveAggressivenessPreference(podcastId: String, aggressiveness: SilenceAggressiveness)
    suspend fun getAggressivenessPreference(podcastId: String): SilenceAggressiveness
    suspend fun saveGlobalSkipSilencePreference(enabled: Boolean)
    suspend fun getGlobalSkipSilencePreference(): Boolean
    suspend fun saveGlobalAggressivenessPreference(aggressiveness: SilenceAggressiveness)
    suspend fun getGlobalAggressivenessPreference(): SilenceAggressiveness
    fun observeSkipSilencePreferences(): Flow<Map<String, Boolean>>
}