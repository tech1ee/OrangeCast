package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.SleepTimer
import dev.orangecast.shared.domain.model.SleepTimerEvent
import dev.orangecast.shared.domain.model.SleepTimerEventType
import dev.orangecast.shared.domain.model.SleepTimerState
import dev.orangecast.shared.domain.repository.PlayerStateRepository
import dev.orangecast.shared.domain.repository.SleepTimerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine

class SleepTimerUseCase(
    private val sleepTimerRepository: SleepTimerRepository,
    private val playerStateRepository: PlayerStateRepository
) {

    private val _timerEvents = MutableSharedFlow<SleepTimerEvent>()
    val timerEvents: Flow<SleepTimerEvent> = _timerEvents.asSharedFlow()

    suspend fun startTimer(durationMinutes: Int) {
        sleepTimerRepository.startTimer(durationMinutes)
        emitEvent(SleepTimerEventType.STARTED, durationMinutes * 60 * 1000L)
    }

    suspend fun startEndOfEpisodeTimer() {
        sleepTimerRepository.startEndOfEpisodeTimer()
        emitEvent(SleepTimerEventType.STARTED, Long.MAX_VALUE)
    }

    suspend fun extendTimer(additionalMinutes: Int) {
        sleepTimerRepository.extendTimer(additionalMinutes)
        val currentTimer = sleepTimerRepository.getCurrentTimer()
        emitEvent(SleepTimerEventType.EXTENDED, currentTimer?.remainingTimeMs ?: 0L)
    }

    suspend fun cancelTimer() {
        sleepTimerRepository.cancelTimer()
        emitEvent(SleepTimerEventType.CANCELLED, 0L)
    }

    suspend fun getCurrentTimer(): SleepTimer? {
        return sleepTimerRepository.getCurrentTimer()
    }

    suspend fun getTimerState(): SleepTimerState {
        val timer = getCurrentTimer() ?: return SleepTimerState.INACTIVE
        
        return when {
            !timer.isActive -> SleepTimerState.INACTIVE
            timer.isExpired -> SleepTimerState.EXPIRED
            timer.shouldStartFadeOut -> SleepTimerState.FADING_OUT
            else -> SleepTimerState.ACTIVE
        }
    }

    suspend fun handleTimerExpiry() {
        val timer = getCurrentTimer()
        if (timer?.isExpired == true) {
            playerStateRepository.updatePlaybackState(isPlaying = false)
            cancelTimer()
            emitEvent(SleepTimerEventType.EXPIRED, 0L)
        }
    }

    suspend fun handleEpisodeEnd() {
        val timer = getCurrentTimer()
        if (timer?.shouldPauseAtEndOfEpisode == true) {
            playerStateRepository.updatePlaybackState(isPlaying = false)
            cancelTimer()
            emitEvent(SleepTimerEventType.EXPIRED, 0L)
        }
    }

    suspend fun checkForFadeOut(): Float? {
        val timer = getCurrentTimer()
        if (timer?.shouldStartFadeOut == true) {
            if (timer.fadeOutProgress == 0f) {
                emitEvent(SleepTimerEventType.FADE_OUT_STARTED, timer.remainingTimeMs)
            }
            return timer.fadeOutProgress
        }
        return null
    }

    suspend fun getRemainingTime(): String {
        val timer = getCurrentTimer()
        return timer?.formatRemainingTime() ?: "No timer"
    }

    suspend fun getProgress(): Float {
        val timer = getCurrentTimer()
        return timer?.progressPercentage ?: 0f
    }

    fun observeTimer(): Flow<SleepTimer?> {
        return sleepTimerRepository.observeSleepTimer()
    }

    fun observeTimerWithPlayerState(): Flow<Pair<SleepTimer?, Boolean>> {
        return combine(
            sleepTimerRepository.observeSleepTimer(),
            playerStateRepository.observePlayerState()
        ) { timer, playerState ->
            Pair(timer, playerState.isPlaying)
        }
    }

    suspend fun getPresetDurations(): List<Pair<String, Int>> {
        return SleepTimer.getPresetDurations()
    }

    suspend fun setDefaultDuration(minutes: Int) {
        sleepTimerRepository.saveDefaultDuration(minutes)
    }

    suspend fun getDefaultDuration(): Int {
        return sleepTimerRepository.getDefaultDuration()
    }

    suspend fun quickStart(): Boolean {
        val defaultDuration = getDefaultDuration()
        return if (defaultDuration > 0) {
            startTimer(defaultDuration)
            true
        } else {
            false
        }
    }

    suspend fun isTimerActive(): Boolean {
        val timer = getCurrentTimer()
        return timer?.isActive == true && !timer.isExpired
    }

    suspend fun willFadeOutSoon(): Boolean {
        val timer = getCurrentTimer()
        return timer?.let { it.isActive && it.remainingTimeMs <= (it.fadeOutDurationMs + 60000) } ?: false // 1 minute warning
    }

    suspend fun getTimeUntilFadeOut(): Long? {
        val timer = getCurrentTimer()
        return timer?.let {
            if (it.isActive) {
                (it.remainingTimeMs - it.fadeOutDurationMs).coerceAtLeast(0L)
            } else null
        }
    }

    private suspend fun emitEvent(type: SleepTimerEventType, remainingTimeMs: Long) {
        _timerEvents.emit(SleepTimerEvent(type, remainingTimeMs))
    }

    suspend fun calculateOptimalVolumeFade(fadeProgress: Float): Float {
        return when {
            fadeProgress <= 0f -> 1.0f
            fadeProgress >= 1f -> 0.0f
            else -> {
                val easedProgress = 1f - (fadeProgress * fadeProgress)
                easedProgress.coerceIn(0f, 1f)
            }
        }
    }

    suspend fun shouldShowExtendNotification(): Boolean {
        val timer = getCurrentTimer()
        return timer?.let { 
            it.isActive && 
            it.remainingTimeMs in 60000L..120000L &&
            !it.shouldPauseAtEndOfEpisode
        } ?: false
    }
}