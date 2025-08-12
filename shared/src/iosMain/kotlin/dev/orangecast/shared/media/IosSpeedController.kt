package dev.orangecast.shared.media

import dev.orangecast.shared.domain.model.PlaybackSpeed
import dev.orangecast.shared.domain.repository.PlayerStateRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVAudioUnitTimePitch
import platform.AVFoundation.AVAudioUnitEQ
import platform.AVFoundation.AVMutableAudioMix
import platform.AVFoundation.AVMutableAudioMixInputParameters

@OptIn(ExperimentalForeignApi::class)
class IosSpeedController(
    private val avPlayer: AVPlayer,
    private val playerStateRepository: PlayerStateRepository,
    private val scope: CoroutineScope
) {
    private var speedUpdateJob: Job? = null
    private var currentAudioMix: AVMutableAudioMix? = null

    fun setPlaybackSpeed(speed: PlaybackSpeed, enableSonicProcessing: Boolean = true) {
        if (enableSonicProcessing && speed != PlaybackSpeed.SPEED_1X) {
            setupAudioMixForSpeed(speed)
        } else {
            avPlayer.rate = speed.value
            avPlayer.currentItem?.audioMix = null
        }
        
        speedUpdateJob?.cancel()
        speedUpdateJob = scope.launch {
            playerStateRepository.updateSpeed(speed.value)
        }
    }

    private fun setupAudioMixForSpeed(speed: PlaybackSpeed) {
        val audioMix = AVMutableAudioMix()
        val inputParameters = AVMutableAudioMixInputParameters()
        
        val timePitchProcessor = AVAudioUnitTimePitch()
        timePitchProcessor.rate = speed.value
        timePitchProcessor.pitch = 0.0f
        timePitchProcessor.overlap = 8.0f
        
        if (speed.isFasterThanNormal()) {
            val eqProcessor = AVAudioUnitEQ(numberOfBands = 3)
            eqProcessor.bands[1].frequency = 2000.0f
            eqProcessor.bands[1].gain = 2.0f
            eqProcessor.bands[1].bypass = false
        }
        
        currentAudioMix = audioMix
        avPlayer.currentItem?.audioMix = audioMix
        avPlayer.rate = 1.0f
    }

    fun getCurrentSpeed(): PlaybackSpeed {
        return PlaybackSpeed.fromValue(avPlayer.rate)
    }

    fun isRateChangeSupported(): Boolean {
        return avPlayer.currentItem?.canPlayFastForward == true || 
               avPlayer.currentItem?.canPlaySlowForward == true
    }

    fun optimizeForVoiceContent(speed: PlaybackSpeed) {
        when {
            speed.isSlowerThanNormal() -> {
                avPlayer.rate = speed.value
                avPlayer.currentItem?.audioMix = null
            }
            speed.isFasterThanNormal() -> {
                setupEnhancedAudioMixForVoice(speed)
            }
            else -> {
                avPlayer.rate = speed.value
                avPlayer.currentItem?.audioMix = null
            }
        }
        
        speedUpdateJob?.cancel()
        speedUpdateJob = scope.launch {
            playerStateRepository.updateSpeed(speed.value)
        }
    }

    private fun setupEnhancedAudioMixForVoice(speed: PlaybackSpeed) {
        val audioMix = AVMutableAudioMix()
        val inputParameters = AVMutableAudioMixInputParameters()
        
        val timePitchProcessor = AVAudioUnitTimePitch()
        timePitchProcessor.rate = speed.value
        timePitchProcessor.pitch = when {
            speed.value >= 2.0f -> -200.0f
            speed.value >= 1.5f -> -100.0f
            else -> -50.0f
        }
        timePitchProcessor.overlap = 16.0f
        
        val eqProcessor = AVAudioUnitEQ(numberOfBands = 5)
        eqProcessor.bands[0].frequency = 80.0f
        eqProcessor.bands[0].gain = -3.0f
        eqProcessor.bands[1].frequency = 500.0f
        eqProcessor.bands[1].gain = 1.0f
        eqProcessor.bands[2].frequency = 1500.0f
        eqProcessor.bands[2].gain = 2.0f
        eqProcessor.bands[3].frequency = 3000.0f
        eqProcessor.bands[3].gain = 1.5f
        eqProcessor.bands[4].frequency = 8000.0f
        eqProcessor.bands[4].gain = -1.0f
        
        currentAudioMix = audioMix
        avPlayer.currentItem?.audioMix = audioMix
        avPlayer.rate = 1.0f
    }

    fun resetAudioProcessing() {
        avPlayer.currentItem?.audioMix = null
        avPlayer.rate = 1.0f
        currentAudioMix = null
    }
}