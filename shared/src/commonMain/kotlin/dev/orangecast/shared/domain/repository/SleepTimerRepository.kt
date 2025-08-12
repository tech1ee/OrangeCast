package dev.orangecast.shared.domain.repository

import dev.orangecast.shared.domain.model.SleepTimer
import kotlinx.coroutines.flow.Flow

interface SleepTimerRepository {
    fun observeSleepTimer(): Flow<SleepTimer?>
    suspend fun startTimer(durationMinutes: Int)
    suspend fun startEndOfEpisodeTimer()
    suspend fun extendTimer(additionalMinutes: Int)
    suspend fun cancelTimer()
    suspend fun getCurrentTimer(): SleepTimer?
    suspend fun saveDefaultDuration(minutes: Int)
    suspend fun getDefaultDuration(): Int
}