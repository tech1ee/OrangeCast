package dev.orangecast.shared.media

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaServiceConnection(private val context: Context) {
    
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    
    private var mediaBrowser: MediaBrowserCompat? = null
    var mediaController: MediaControllerCompat? = null
        private set
    
    private val connectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            mediaBrowser?.sessionToken?.let { token ->
                mediaController = MediaControllerCompat(context, token)
                _isConnected.value = true
            }
        }
        
        override fun onConnectionSuspended() {
            _isConnected.value = false
        }
        
        override fun onConnectionFailed() {
            _isConnected.value = false
        }
    }
    
    fun connect() {
        if (mediaBrowser == null) {
            mediaBrowser = MediaBrowserCompat(
                context,
                ComponentName(context, AudioPlaybackService::class.java),
                connectionCallbacks,
                null
            )
        }
        
        mediaBrowser?.connect()
    }
    
    fun disconnect() {
        mediaBrowser?.disconnect()
        mediaController = null
        _isConnected.value = false
    }
    
    fun startService() {
        val intent = Intent(context, AudioPlaybackService::class.java)
        context.startService(intent)
    }
    
    fun stopService() {
        val intent = Intent(context, AudioPlaybackService::class.java)
        context.stopService(intent)
    }
}