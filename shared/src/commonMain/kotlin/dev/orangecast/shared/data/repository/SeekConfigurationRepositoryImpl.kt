package dev.orangecast.shared.data.repository

import dev.orangecast.shared.domain.model.SeekConfiguration
import dev.orangecast.shared.domain.repository.SeekConfigurationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SeekConfigurationRepositoryImpl : SeekConfigurationRepository {

    private var currentConfig = SeekConfiguration.createDefault()
    private val mutex = Mutex()
    private val _configFlow = MutableStateFlow(currentConfig)

    override suspend fun saveSeekConfiguration(config: SeekConfiguration) {
        mutex.withLock {
            currentConfig = config
            _configFlow.value = config
        }
    }

    override suspend fun getSeekConfiguration(): SeekConfiguration {
        return mutex.withLock {
            currentConfig
        }
    }

    override suspend fun saveForwardInterval(intervalMs: Long) {
        mutex.withLock {
            currentConfig = currentConfig.copy(forwardIntervalMs = intervalMs)
            _configFlow.value = currentConfig
        }
    }

    override suspend fun saveBackwardInterval(intervalMs: Long) {
        mutex.withLock {
            currentConfig = currentConfig.copy(backwardIntervalMs = intervalMs)
            _configFlow.value = currentConfig
        }
    }

    override suspend fun enableSmartRewind(enabled: Boolean) {
        mutex.withLock {
            currentConfig = currentConfig.copy(enableSmartRewind = enabled)
            _configFlow.value = currentConfig
        }
    }

    override suspend fun enableHapticFeedback(enabled: Boolean) {
        mutex.withLock {
            currentConfig = currentConfig.copy(enableHapticFeedback = enabled)
            _configFlow.value = currentConfig
        }
    }

    override fun observeSeekConfiguration(): Flow<SeekConfiguration> {
        return _configFlow.asStateFlow()
    }
}