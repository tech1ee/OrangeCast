package dev.orangecast.shared.data.repository

import dev.orangecast.shared.domain.model.PlaybackSpeed
import dev.orangecast.shared.domain.repository.PlaybackSpeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PlaybackSpeedRepositoryImpl : PlaybackSpeedRepository {

    private val speedPreferences = mutableMapOf<String, PlaybackSpeed>()
    private var globalSpeed = PlaybackSpeed.SPEED_1X
    private val mutex = Mutex()

    private val _speedPreferencesFlow = MutableStateFlow<Map<String, PlaybackSpeed>>(emptyMap())

    override suspend fun savePreferredSpeed(podcastId: String, speed: PlaybackSpeed) {
        mutex.withLock {
            speedPreferences[podcastId] = speed
            _speedPreferencesFlow.value = speedPreferences.toMap()
        }
    }

    override suspend fun getPreferredSpeed(podcastId: String): PlaybackSpeed {
        return mutex.withLock {
            speedPreferences[podcastId] ?: globalSpeed
        }
    }

    override suspend fun getGlobalPreferredSpeed(): PlaybackSpeed {
        return mutex.withLock {
            globalSpeed
        }
    }

    override suspend fun saveGlobalPreferredSpeed(speed: PlaybackSpeed) {
        mutex.withLock {
            globalSpeed = speed
        }
    }

    override fun observeSpeedPreferences(): Flow<Map<String, PlaybackSpeed>> {
        return _speedPreferencesFlow.asStateFlow()
    }
}