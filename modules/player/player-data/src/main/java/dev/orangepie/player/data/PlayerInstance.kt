package dev.orangepie.player.data

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.upstream.DefaultAllocator
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PlayerInstance @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {

    @SuppressLint("UnsafeOptInUsageError")
    fun initPlayer(): Player {
        val loadControl = DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, 64 * 1024))
            .setBufferDurationsMs(
                1000,     // minBufferMs - start playback after 1s
                20000,    // maxBufferMs - maximum 20s buffer
                1000,     // bufferForPlaybackMs - 1s for initial playback
                1000      // bufferForPlaybackAfterRebufferMs - 1s after rebuffering
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val builder = ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .setLooper(context.mainLooper)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setUsePlatformDiagnostics(false)
        }

        return builder.build()
    }
}