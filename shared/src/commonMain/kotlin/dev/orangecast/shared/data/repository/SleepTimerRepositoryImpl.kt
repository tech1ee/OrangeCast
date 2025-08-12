package dev.orangecast.shared.data.repository

import dev.orangecast.shared.domain.model.SleepTimer
import dev.orangecast.shared.domain.repository.SleepTimerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SleepTimerRepositoryImpl : SleepTimerRepository {

    private var currentTimer: SleepTimer? = null
    private var defaultDurationMinutes: Int = 15 // Default to 15 minutes
    private val mutex = Mutex()
    
    private val _timerFlow = MutableStateFlow<SleepTimer?>(null)

    override fun observeSleepTimer(): Flow<SleepTimer?> {
        return _timerFlow.asStateFlow()
    }

    override suspend fun startTimer(durationMinutes: Int) {
        mutex.withLock {
            currentTimer = SleepTimer.create(durationMinutes)
            _timerFlow.value = currentTimer
        }
    }

    override suspend fun startEndOfEpisodeTimer() {
        mutex.withLock {
            currentTimer = SleepTimer.createEndOfEpisode()
            _timerFlow.value = currentTimer
        }
    }

    override suspend fun extendTimer(additionalMinutes: Int) {
        mutex.withLock {
            currentTimer = currentTimer?.extend(additionalMinutes)
            _timerFlow.value = currentTimer
        }
    }

    override suspend fun cancelTimer() {
        mutex.withLock {
            currentTimer = currentTimer?.cancel()
            _timerFlow.value = null
            currentTimer = null
        }
    }

    override suspend fun getCurrentTimer(): SleepTimer? {
        return mutex.withLock {
            // Check if timer has expired and clean up
            if (currentTimer?.isExpired == true) {
                currentTimer = null
                _timerFlow.value = null
            }
            currentTimer
        }
    }

    override suspend fun saveDefaultDuration(minutes: Int) {
        mutex.withLock {
            defaultDurationMinutes = minutes.coerceIn(1, 240) // 1 minute to 4 hours
        }
    }

    override suspend fun getDefaultDuration(): Int {
        return mutex.withLock {
            defaultDurationMinutes
        }
    }
}