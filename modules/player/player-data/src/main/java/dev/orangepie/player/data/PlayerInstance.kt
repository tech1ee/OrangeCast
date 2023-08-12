package dev.orangepie.player.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PlayerInstance @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {

    @SuppressLint("UnsafeOptInUsageError")
    fun initPlayer(): Player {
        return ExoPlayer.Builder(context)
            .setLooper(context.mainLooper)
            .build()
    }
}