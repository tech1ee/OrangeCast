package dev.orangecast.shared.media

import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import dev.orangecast.shared.domain.model.PlaybackSpeed
import dev.orangecast.shared.domain.repository.PlayerStateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AndroidSpeedController(
    private val exoPlayer: ExoPlayer,
    private val playerStateRepository: PlayerStateRepository,
    private val scope: CoroutineScope
) {
    private var speedUpdateJob: Job? = null

    fun setPlaybackSpeed(speed: PlaybackSpeed, maintainPitch: Boolean = true) {
        val playbackParameters = PlaybackParameters(speed.value, if (maintainPitch) 1.0f else speed.value)
        exoPlayer.playbackParameters = playbackParameters
        
        speedUpdateJob?.cancel()
        speedUpdateJob = scope.launch {
            playerStateRepository.updateSpeed(speed.value)
        }
    }

    fun getCurrentPlaybackParameters(): PlaybackParameters {
        return exoPlayer.playbackParameters
    }

    fun getCurrentSpeed(): PlaybackSpeed {
        return PlaybackSpeed.fromValue(exoPlayer.playbackParameters.speed)
    }

    fun isPlaybackParametersSupported(): Boolean {
        return try {
            exoPlayer.playbackParameters = PlaybackParameters(1.0f)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun optimizeForPodcastPlayback(speed: PlaybackSpeed): PlaybackParameters {
        return when {
            speed.isSlowerThanNormal() -> {
                PlaybackParameters(speed.value, 1.0f)
            }
            speed.isFasterThanNormal() -> {
                val adjustedPitch = (1.0f + (speed.value - 1.0f) * 0.3f).coerceAtMost(1.2f)
                PlaybackParameters(speed.value, adjustedPitch)
            }
            else -> {
                PlaybackParameters(speed.value, 1.0f)
            }
        }
    }

    fun setOptimizedPlaybackSpeed(speed: PlaybackSpeed) {
        val optimizedParameters = optimizeForPodcastPlayback(speed)
        exoPlayer.playbackParameters = optimizedParameters
        
        speedUpdateJob?.cancel()
        speedUpdateJob = scope.launch {
            playerStateRepository.updateSpeed(speed.value)
        }
    }
}