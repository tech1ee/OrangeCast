package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.SeekAction
import dev.orangecast.shared.domain.model.SeekActionType
import dev.orangecast.shared.domain.model.SeekConfiguration
import dev.orangecast.shared.domain.model.SmartRewindState
import dev.orangecast.shared.domain.repository.PlayerStateRepository
import dev.orangecast.shared.domain.repository.SeekConfigurationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SeekControlUseCase(
    private val playerStateRepository: PlayerStateRepository,
    private val seekConfigurationRepository: SeekConfigurationRepository
) {

    private var smartRewindState = SmartRewindState()
    private val _recentSeekActions = MutableStateFlow<List<SeekAction>>(emptyList())

    suspend fun seekForward(): SeekAction {
        val currentState = playerStateRepository.getCurrentState()
        val config = seekConfigurationRepository.getSeekConfiguration()
        
        val currentPosition = currentState.currentPosition
        val targetPosition = (currentPosition + config.forwardIntervalMs).coerceAtMost(currentState.duration)
        
        playerStateRepository.updatePosition(targetPosition)
        
        val action = SeekAction(
            type = SeekActionType.FORWARD,
            targetPositionMs = targetPosition,
            intervalMs = config.forwardIntervalMs
        )
        
        recordSeekAction(action)
        return action
    }

    suspend fun seekBackward(): SeekAction {
        val currentState = playerStateRepository.getCurrentState()
        val config = seekConfigurationRepository.getSeekConfiguration()
        val currentTime = System.currentTimeMillis()
        
        smartRewindState = smartRewindState.incrementTaps(currentTime)
        
        val intervalMs = if (smartRewindState.shouldActivateSmartRewind(config, currentTime)) {
            val smartInterval = (config.backwardIntervalMs * config.smartRewindMultiplier).toLong()
            smartInterval
        } else {
            config.backwardIntervalMs
        }
        
        val currentPosition = currentState.currentPosition
        val targetPosition = (currentPosition - intervalMs).coerceAtLeast(0L)
        
        playerStateRepository.updatePosition(targetPosition)
        
        val action = SeekAction(
            type = if (smartRewindState.shouldActivateSmartRewind(config, currentTime)) {
                SeekActionType.SMART_REWIND
            } else {
                SeekActionType.BACKWARD
            },
            targetPositionMs = targetPosition,
            intervalMs = intervalMs,
            isSmartRewind = smartRewindState.shouldActivateSmartRewind(config, currentTime)
        )
        
        recordSeekAction(action)
        return action
    }

    suspend fun seekToPosition(positionMs: Long): SeekAction {
        val currentState = playerStateRepository.getCurrentState()
        val targetPosition = positionMs.coerceIn(0L, currentState.duration)
        
        playerStateRepository.updatePosition(targetPosition)
        resetSmartRewindState()
        
        val action = SeekAction(
            type = SeekActionType.POSITION_SET,
            targetPositionMs = targetPosition,
            intervalMs = 0L
        )
        
        recordSeekAction(action)
        return action
    }

    suspend fun seekToPercentage(percentage: Float): SeekAction {
        val currentState = playerStateRepository.getCurrentState()
        val targetPosition = (currentState.duration * percentage.coerceIn(0f, 1f)).toLong()
        return seekToPosition(targetPosition)
    }

    suspend fun skipChapter(forward: Boolean): SeekAction? {
        return null
    }

    suspend fun customSeek(intervalMs: Long, forward: Boolean): SeekAction {
        val currentState = playerStateRepository.getCurrentState()
        val currentPosition = currentState.currentPosition
        
        val targetPosition = if (forward) {
            (currentPosition + intervalMs).coerceAtMost(currentState.duration)
        } else {
            (currentPosition - intervalMs).coerceAtLeast(0L)
        }
        
        playerStateRepository.updatePosition(targetPosition)
        
        val action = SeekAction(
            type = if (forward) SeekActionType.FORWARD else SeekActionType.BACKWARD,
            targetPositionMs = targetPosition,
            intervalMs = intervalMs
        )
        
        recordSeekAction(action)
        return action
    }

    suspend fun getSeekConfiguration(): SeekConfiguration {
        return seekConfigurationRepository.getSeekConfiguration()
    }

    suspend fun updateSeekConfiguration(config: SeekConfiguration) {
        seekConfigurationRepository.saveSeekConfiguration(config)
    }

    suspend fun setForwardInterval(seconds: Int) {
        val intervalMs = (seconds * 1000L).coerceIn(5000L, 120000L) // 5 seconds to 2 minutes
        seekConfigurationRepository.saveForwardInterval(intervalMs)
    }

    suspend fun setBackwardInterval(seconds: Int) {
        val intervalMs = (seconds * 1000L).coerceIn(5000L, 60000L) // 5 seconds to 1 minute
        seekConfigurationRepository.saveBackwardInterval(intervalMs)
    }

    suspend fun toggleSmartRewind() {
        val currentConfig = seekConfigurationRepository.getSeekConfiguration()
        seekConfigurationRepository.enableSmartRewind(!currentConfig.enableSmartRewind)
    }

    suspend fun toggleHapticFeedback() {
        val currentConfig = seekConfigurationRepository.getSeekConfiguration()
        seekConfigurationRepository.enableHapticFeedback(!currentConfig.enableHapticFeedback)
    }

    fun observeSeekConfiguration(): Flow<SeekConfiguration> {
        return seekConfigurationRepository.observeSeekConfiguration()
    }

    fun observeRecentSeekActions(): Flow<List<SeekAction>> {
        return _recentSeekActions.asStateFlow()
    }

    fun resetSmartRewindState() {
        smartRewindState = smartRewindState.reset()
    }

    private fun recordSeekAction(action: SeekAction) {
        val currentActions = _recentSeekActions.value.toMutableList()
        currentActions.add(action)
        
        if (currentActions.size > 10) {
            currentActions.removeAt(0)
        }
        
        _recentSeekActions.value = currentActions
    }

    suspend fun calculateOptimalSeekIntervals(): Pair<Int, Int> {
        val recentActions = _recentSeekActions.value
        
        if (recentActions.isEmpty()) {
            return Pair(30, 15) // Default intervals
        }
        
        val forwardActions = recentActions.filter { it.type == SeekActionType.FORWARD }
        val backwardActions = recentActions.filter { it.type == SeekActionType.BACKWARD }
        
        val optimalForward = if (forwardActions.isNotEmpty()) {
            val avgInterval = forwardActions.map { it.intervalMs }.average()
            (avgInterval / 1000).toInt().coerceIn(15, 60)
        } else {
            30
        }
        
        val optimalBackward = if (backwardActions.isNotEmpty()) {
            val avgInterval = backwardActions.map { it.intervalMs }.average()
            (avgInterval / 1000).toInt().coerceIn(10, 30)
        } else {
            15
        }
        
        return Pair(optimalForward, optimalBackward)
    }

    suspend fun getSeekAccuracy(): Float {
        val currentState = playerStateRepository.getCurrentState()
        val recentActions = _recentSeekActions.value.takeLast(5)
        
        if (recentActions.isEmpty()) return 1.0f
        
        var accuracySum = 0f
        
        for (action in recentActions) {
            val targetAccuracy = when (action.type) {
                SeekActionType.POSITION_SET -> 1.0f // Direct position setting is always accurate
                else -> {
                    val expectedPosition = action.targetPositionMs
                    val actualPosition = currentState.currentPosition
                    val difference = kotlin.math.abs(expectedPosition - actualPosition)
                    (1.0f - (difference.toFloat() / action.intervalMs.toFloat())).coerceAtLeast(0f)
                }
            }
            accuracySum += targetAccuracy
        }
        
        return (accuracySum / recentActions.size).coerceIn(0f, 1f)
    }
}