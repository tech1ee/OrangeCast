package dev.orangecast.shared.data.repository

import dev.orangecast.shared.domain.model.SilenceAggressiveness
import dev.orangecast.shared.domain.repository.SilenceDetectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SilenceDetectionRepositoryImpl : SilenceDetectionRepository {

    private val skipSilencePreferences = mutableMapOf<String, Boolean>()
    private val aggressivenessPreferences = mutableMapOf<String, SilenceAggressiveness>()
    private var globalSkipSilenceEnabled = false
    private var globalAggressiveness = SilenceAggressiveness.MEDIUM
    private val mutex = Mutex()

    private val _skipSilencePreferencesFlow = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    override suspend fun saveSkipSilencePreference(podcastId: String, enabled: Boolean) {
        mutex.withLock {
            skipSilencePreferences[podcastId] = enabled
            _skipSilencePreferencesFlow.value = skipSilencePreferences.toMap()
        }
    }

    override suspend fun getSkipSilencePreference(podcastId: String): Boolean {
        return mutex.withLock {
            skipSilencePreferences[podcastId] ?: globalSkipSilenceEnabled
        }
    }

    override suspend fun saveAggressivenessPreference(podcastId: String, aggressiveness: SilenceAggressiveness) {
        mutex.withLock {
            aggressivenessPreferences[podcastId] = aggressiveness
        }
    }

    override suspend fun getAggressivenessPreference(podcastId: String): SilenceAggressiveness {
        return mutex.withLock {
            aggressivenessPreferences[podcastId] ?: globalAggressiveness
        }
    }

    override suspend fun saveGlobalSkipSilencePreference(enabled: Boolean) {
        mutex.withLock {
            globalSkipSilenceEnabled = enabled
        }
    }

    override suspend fun getGlobalSkipSilencePreference(): Boolean {
        return mutex.withLock {
            globalSkipSilenceEnabled
        }
    }

    override suspend fun saveGlobalAggressivenessPreference(aggressiveness: SilenceAggressiveness) {
        mutex.withLock {
            globalAggressiveness = aggressiveness
        }
    }

    override suspend fun getGlobalAggressivenessPreference(): SilenceAggressiveness {
        return mutex.withLock {
            globalAggressiveness
        }
    }

    override fun observeSkipSilencePreferences(): Flow<Map<String, Boolean>> {
        return _skipSilencePreferencesFlow.asStateFlow()
    }
}