package dev.orangecast.shared.media

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import dev.orangecast.shared.domain.usecase.AdaptiveBufferUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.net.URL

class AudioPlaybackService : MediaBrowserServiceCompat() {
    
    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "orangecast_playback"
        const val MEDIA_SESSION_TAG = "OrangeCastMediaSession"
        const val MEDIA_ROOT_ID = "media_root_id"
        const val EMPTY_MEDIA_ROOT_ID = "empty_root_id"
    }

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var notificationManager: NotificationManager
    private lateinit var audioManager: AudioManager
    
    private val exoPlayer: ExoPlayer by inject()
    private val adaptiveBufferUseCase: AdaptiveBufferUseCase by inject()
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    
    private var audioFocusRequest: AudioFocusRequest? = null
    private var currentEpisodeTitle: String = ""
    private var currentPodcastTitle: String = ""
    private var currentImageUrl: String? = null
    private var currentArtworkBitmap: Bitmap? = null

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                exoPlayer.volume = 1.0f
                if (!exoPlayer.isPlaying) {
                    exoPlayer.play()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                exoPlayer.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                exoPlayer.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                exoPlayer.volume = 0.3f
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        createNotificationChannel()
        
        val sessionActivityPendingIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let { intent ->
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        mediaSession = MediaSessionCompat(this, MEDIA_SESSION_TAG).apply {
            setSessionActivity(sessionActivityPendingIntent)
            isActive = true
        }
        
        sessionToken = mediaSession.sessionToken
        
        stateBuilder = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_STOP or
                PlaybackStateCompat.ACTION_SEEK_TO or
                PlaybackStateCompat.ACTION_FAST_FORWARD or
                PlaybackStateCompat.ACTION_REWIND
            )
        
        mediaSession.setCallback(MediaSessionCallback())
        mediaSession.setPlaybackState(stateBuilder.build())
        
        exoPlayer.addListener(PlayerEventListener())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        abandonAudioFocus()
        mediaSession.isActive = false
        mediaSession.release()
        exoPlayer.release()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return if (clientPackageName == packageName) {
            BrowserRoot(MEDIA_ROOT_ID, null)
        } else {
            BrowserRoot(EMPTY_MEDIA_ROOT_ID, null)
        }
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(mutableListOf())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Audio playback controls"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(state: Int): Notification {
        val controller = mediaSession.controller
        val metadata = controller.metadata
        val description = metadata?.description
        
        val playPauseAction = if (state == PlaybackStateCompat.STATE_PLAYING) {
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause,
                "Pause",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_PAUSE
                )
            )
        } else {
            NotificationCompat.Action(
                android.R.drawable.ic_media_play,
                "Play",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_PLAY
                )
            )
        }
        
        val contentIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let { intent ->
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        
        val stopIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(
            this,
            PlaybackStateCompat.ACTION_STOP
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle(currentEpisodeTitle)
            setContentText(currentPodcastTitle)
            setSubText("OrangeCast")
            setLargeIcon(currentArtworkBitmap)
            setContentIntent(contentIntent)
            setDeleteIntent(stopIntent)
            setOnlyAlertOnce(true)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSmallIcon(android.R.drawable.ic_media_play)
            
            addAction(
                android.R.drawable.ic_media_rew,
                "Rewind",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this@AudioPlaybackService,
                    PlaybackStateCompat.ACTION_REWIND
                )
            )
            addAction(playPauseAction)
            addAction(
                android.R.drawable.ic_media_ff,
                "Fast Forward",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this@AudioPlaybackService,
                    PlaybackStateCompat.ACTION_FAST_FORWARD
                )
            )
            
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(stopIntent)
            )
        }.build()
    }

    private fun requestAudioFocus(): Boolean {
        val request = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                setAcceptsDelayedFocusGain(true)
                setOnAudioFocusChangeListener(audioFocusChangeListener)
            }.build()
            
            audioManager.requestAudioFocus(audioFocusRequest!!)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
        
        return request == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let {
                audioManager.abandonAudioFocusRequest(it)
            }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(audioFocusChangeListener)
        }
    }

    fun updateMetadata(title: String, podcast: String, imageUrl: String?) {
        currentEpisodeTitle = title
        currentPodcastTitle = podcast
        currentImageUrl = imageUrl
        
        serviceScope.launch {
            imageUrl?.let { url ->
                currentArtworkBitmap = loadBitmapFromUrl(url)
            }
            
            if (exoPlayer.isPlaying) {
                val notification = buildNotification(PlaybackStateCompat.STATE_PLAYING)
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
        }
    }

    private suspend fun loadBitmapFromUrl(url: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openStream()
                android.graphics.BitmapFactory.decodeStream(connection)
            } catch (e: Exception) {
                null
            }
        }
    }

    private inner class MediaSessionCallback : MediaSessionCompat.Callback() {
        
        override fun onPlay() {
            if (requestAudioFocus()) {
                mediaSession.isActive = true
                exoPlayer.play()
                
                val notification = buildNotification(PlaybackStateCompat.STATE_PLAYING)
                ContextCompat.startForegroundService(
                    this@AudioPlaybackService,
                    Intent(this@AudioPlaybackService, AudioPlaybackService::class.java)
                )
                startForeground(NOTIFICATION_ID, notification)
            }
        }

        override fun onPause() {
            exoPlayer.pause()
            
            val notification = buildNotification(PlaybackStateCompat.STATE_PAUSED)
            notificationManager.notify(NOTIFICATION_ID, notification)
            
            stopForeground(false)
        }

        override fun onStop() {
            abandonAudioFocus()
            mediaSession.isActive = false
            
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            
            stopForeground(true)
            stopSelf()
        }

        override fun onSeekTo(pos: Long) {
            exoPlayer.seekTo(pos)
        }

        override fun onFastForward() {
            val currentPosition = exoPlayer.currentPosition
            val duration = exoPlayer.duration
            val newPosition = (currentPosition + 30000).coerceAtMost(duration)
            exoPlayer.seekTo(newPosition)
        }

        override fun onRewind() {
            val currentPosition = exoPlayer.currentPosition
            val newPosition = (currentPosition - 15000).coerceAtLeast(0)
            exoPlayer.seekTo(newPosition)
        }
    }

    private inner class PlayerEventListener : Player.Listener {
        
        override fun onPlaybackStateChanged(playbackState: Int) {
            val state = when (playbackState) {
                Player.STATE_IDLE -> PlaybackStateCompat.STATE_NONE
                Player.STATE_BUFFERING -> PlaybackStateCompat.STATE_BUFFERING
                Player.STATE_READY -> {
                    if (exoPlayer.playWhenReady) {
                        PlaybackStateCompat.STATE_PLAYING
                    } else {
                        PlaybackStateCompat.STATE_PAUSED
                    }
                }
                Player.STATE_ENDED -> PlaybackStateCompat.STATE_STOPPED
                else -> PlaybackStateCompat.STATE_NONE
            }
            
            setPlaybackState(state)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            val state = if (isPlaying) {
                PlaybackStateCompat.STATE_PLAYING
            } else {
                PlaybackStateCompat.STATE_PAUSED
            }
            
            setPlaybackState(state)
        }

        override fun onPlayerError(error: PlaybackException) {
            setPlaybackState(PlaybackStateCompat.STATE_ERROR)
        }
        
        private fun setPlaybackState(state: Int) {
            val playbackState = stateBuilder
                .setState(state, exoPlayer.currentPosition, 1.0f)
                .build()
            
            mediaSession.setPlaybackState(playbackState)
            
            if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_PAUSED) {
                val notification = buildNotification(state)
                
                if (state == PlaybackStateCompat.STATE_PLAYING) {
                    startForeground(NOTIFICATION_ID, notification)
                } else {
                    notificationManager.notify(NOTIFICATION_ID, notification)
                    stopForeground(false)
                }
            }
        }
    }
}