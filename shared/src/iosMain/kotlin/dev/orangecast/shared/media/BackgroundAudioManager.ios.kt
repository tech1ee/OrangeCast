package dev.orangecast.shared.media

import dev.orangecast.shared.domain.model.AudioTrack
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.*
import platform.Foundation.*
import platform.MediaPlayer.*
import platform.UIKit.UIApplication

@OptIn(ExperimentalForeignApi::class)
class BackgroundAudioManager {
    
    private val nowPlayingInfoCenter = MPNowPlayingInfoCenter.defaultCenter()
    private var remoteCommandCenter = MPRemoteCommandCenter.sharedCommandCenter()
    
    private var playCommand: (() -> Unit)? = null
    private var pauseCommand: (() -> Unit)? = null
    private var seekCommand: ((Double) -> Unit)? = null
    private var skipForwardCommand: (() -> Unit)? = null
    private var skipBackwardCommand: (() -> Unit)? = null
    
    fun setupAudioSession(): Boolean {
        return try {
            val audioSession = AVAudioSession.sharedInstance()
            
            audioSession.setCategoryWithOptionsError(
                AVAudioSessionCategoryPlayback,
                AVAudioSessionCategoryOptionAllowBluetooth or
                AVAudioSessionCategoryOptionAllowBluetoothA2DP or
                AVAudioSessionCategoryOptionAllowAirPlay,
                null
            )
            
            audioSession.setModeError(AVAudioSessionModeSpokenAudio, null)
            
            val success = audioSession.setActiveError(true, null)
            
            if (success) {
                setupAudioSessionNotifications()
            }
            
            success
        } catch (e: Exception) {
            false
        }
    }
    
    fun setupRemoteControls() {
        remoteCommandCenter.playCommand.addTargetWithHandler { _ ->
            playCommand?.invoke()
            MPRemoteCommandHandlerStatusSuccess
        }
        
        remoteCommandCenter.pauseCommand.addTargetWithHandler { _ ->
            pauseCommand?.invoke()
            MPRemoteCommandHandlerStatusSuccess
        }
        
        remoteCommandCenter.skipForwardCommand.addTargetWithHandler { _ ->
            skipForwardCommand?.invoke()
            MPRemoteCommandHandlerStatusSuccess
        }
        remoteCommandCenter.skipForwardCommand.preferredIntervals = listOf(NSNumber(30.0))
        
  
        remoteCommandCenter.skipBackwardCommand.addTargetWithHandler { _ ->
            skipBackwardCommand?.invoke()
            MPRemoteCommandHandlerStatusSuccess
        }
        remoteCommandCenter.skipBackwardCommand.preferredIntervals = listOf(NSNumber(15.0))
        
        remoteCommandCenter.changePlaybackPositionCommand.addTargetWithHandler { event ->
            val seekEvent = event as? MPChangePlaybackPositionCommandEvent
            seekEvent?.positionTime?.let { position ->
                seekCommand?.invoke(position)
            }
            MPRemoteCommandHandlerStatusSuccess
        }
        
        remoteCommandCenter.playCommand.enabled = true
        remoteCommandCenter.pauseCommand.enabled = true
        remoteCommandCenter.skipForwardCommand.enabled = true
        remoteCommandCenter.skipBackwardCommand.enabled = true
        remoteCommandCenter.changePlaybackPositionCommand.enabled = true
    }
    
    fun updateNowPlayingInfo(
        track: AudioTrack?,
        position: Double,
        duration: Double,
        playbackRate: Double
    ) {
        if (track == null) {
            nowPlayingInfoCenter.nowPlayingInfo = null
            return
        }
        
        val nowPlayingInfo = mutableMapOf<String, Any>().apply {
            put(MPMediaItemPropertyTitle, track.title)
            put(MPMediaItemPropertyArtist, track.artist.takeIf { it.isNotEmpty() } ?: "Unknown Artist")
            put(MPMediaItemPropertyAlbumTitle, track.albumTitle.takeIf { it.isNotEmpty() } ?: "")
            put(MPNowPlayingInfoPropertyElapsedPlaybackTime, position)
            put(MPMediaItemPropertyPlaybackDuration, duration)
            put(MPNowPlayingInfoPropertyPlaybackRate, playbackRate)
            put(MPNowPlayingInfoPropertyDefaultPlaybackRate, 1.0)
            
            track.imageUrl?.let { artworkUrl ->
                put(MPNowPlayingInfoPropertyPlaybackQueueIndex, 0)
                put(MPNowPlayingInfoPropertyPlaybackQueueCount, 1)
            }
        }
        
        nowPlayingInfoCenter.nowPlayingInfo = nowPlayingInfo.toMap()
    }
    
    fun setPlayCommandHandler(handler: () -> Unit) {
        playCommand = handler
    }
    
    fun setPauseCommandHandler(handler: () -> Unit) {
        pauseCommand = handler
    }
    
    fun setSeekCommandHandler(handler: (Double) -> Unit) {
        seekCommand = handler
    }
    
    fun setSkipForwardCommandHandler(handler: () -> Unit) {
        skipForwardCommand = handler
    }
    
    fun setSkipBackwardCommandHandler(handler: () -> Unit) {
        skipBackwardCommand = handler
    }
    
    private fun setupAudioSessionNotifications() {
        val notificationCenter = NSNotificationCenter.defaultCenter()
        
        notificationCenter.addObserverForNameObjectQueueUsingBlock(
            AVAudioSessionInterruptionNotification,
            null,
            NSOperationQueue.mainQueue()
        ) { notification ->
            val userInfo = notification.userInfo
            val interruptionType = (userInfo?.get(AVAudioSessionInterruptionTypeKey) as? NSNumber)?.intValue
            
            when (interruptionType) {
                AVAudioSessionInterruptionTypeBegan.toInt() -> {
                    handleAudioInterruption(true)
                }
                AVAudioSessionInterruptionTypeEnded.toInt() -> {
                    val options = (userInfo?.get(AVAudioSessionInterruptionOptionKey) as? NSNumber)?.intValue ?: 0
                    if (options and AVAudioSessionInterruptionOptionShouldResume.toInt() != 0) {
                        handleAudioInterruption(false)
                    }
                }
            }
        }
        
        notificationCenter.addObserverForNameObjectQueueUsingBlock(
            AVAudioSessionRouteChangeNotification,
            null,
            NSOperationQueue.mainQueue()
        ) { notification ->
            val userInfo = notification.userInfo
            val reason = (userInfo?.get(AVAudioSessionRouteChangeReasonKey) as? NSNumber)?.intValue
            
            if (reason == AVAudioSessionRouteChangeReasonOldDeviceUnavailable.toInt()) {
                pauseCommand?.invoke()
            }
        }
    }
    
    fun updateNowPlayingInfoWithArtwork(
        track: AudioTrack?,
        position: Double,
        duration: Double,
        playbackRate: Double,
        artworkData: NSData?
    ) {
        if (track == null) {
            nowPlayingInfoCenter.nowPlayingInfo = null
            return
        }
        
        val nowPlayingInfo = mutableMapOf<String, Any>().apply {
            put(MPMediaItemPropertyTitle, track.title)
            put(MPMediaItemPropertyArtist, track.artist.takeIf { it.isNotEmpty() } ?: "Unknown Artist")
            put(MPMediaItemPropertyAlbumTitle, track.albumTitle.takeIf { it.isNotEmpty() } ?: "")
            put(MPNowPlayingInfoPropertyElapsedPlaybackTime, position)
            put(MPMediaItemPropertyPlaybackDuration, duration)
            put(MPNowPlayingInfoPropertyPlaybackRate, playbackRate)
            put(MPNowPlayingInfoPropertyDefaultPlaybackRate, 1.0)
            put(MPNowPlayingInfoPropertyPlaybackQueueIndex, 0)
            put(MPNowPlayingInfoPropertyPlaybackQueueCount, 1)
            
            artworkData?.let { data ->
                val image = platform.UIKit.UIImage.imageWithData(data)
                image?.let {
                    val artwork = MPMediaItemArtwork(boundsSize = it.size) { _ -> it }
                    put(MPMediaItemPropertyArtwork, artwork)
                }
            }
        }
        
        nowPlayingInfoCenter.nowPlayingInfo = nowPlayingInfo.toMap()
    }
    
    fun setNowPlayingPlaybackState(isPlaying: Boolean, position: Double = 0.0, rate: Double = 1.0) {
        val nowPlayingInfo = nowPlayingInfoCenter.nowPlayingInfo?.toMutableMap() ?: mutableMapOf()
        nowPlayingInfo[MPNowPlayingInfoPropertyElapsedPlaybackTime] = position
        nowPlayingInfo[MPNowPlayingInfoPropertyPlaybackRate] = if (isPlaying) rate else 0.0
        
        nowPlayingInfoCenter.nowPlayingInfo = nowPlayingInfo.toMap()
    }
    
    fun deactivateAudioSession() {
        try {
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setActiveError(false, null)
            
            NSNotificationCenter.defaultCenter().removeObserver(this)
        } catch (e: Exception) {
        }
    }
    
    fun handleAudioInterruption(begin: Boolean) {
        if (begin) {
            pauseCommand?.invoke()
            setNowPlayingPlaybackState(isPlaying = false)
        } else {
            playCommand?.invoke()
            setNowPlayingPlaybackState(isPlaying = true)
        }
    }
    
    fun configureAudioSessionForPodcast() {
        try {
            val audioSession = AVAudioSession.sharedInstance()
            
            audioSession.setCategoryWithOptionsError(
                AVAudioSessionCategoryPlayback,
                AVAudioSessionCategoryOptionAllowBluetooth or 
                AVAudioSessionCategoryOptionAllowBluetoothA2DP or
                AVAudioSessionCategoryOptionAllowAirPlay or
                AVAudioSessionCategoryOptionDuckOthers,
                null
            )
            
            audioSession.setModeError(AVAudioSessionModeSpokenAudio, null)
            audioSession.setActiveError(true, null)
        } catch (e: Exception) {
        }
    }
}