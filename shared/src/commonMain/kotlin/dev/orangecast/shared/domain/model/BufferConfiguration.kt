package dev.orangecast.shared.domain.model

data class BufferConfiguration(
    val minBufferMs: Int,
    val maxBufferMs: Int,
    val bufferForPlaybackMs: Int,
    val bufferForPlaybackAfterRebufferMs: Int,
    val prioritizeTimeOverSize: Boolean
) {
    companion object {
        fun createOptimalConfiguration(networkType: NetworkType): BufferConfiguration {
            return when (networkType) {
                NetworkType.WIFI -> BufferConfiguration(
                    minBufferMs = 1000,
                    maxBufferMs = 20000,
                    bufferForPlaybackMs = 1000,
                    bufferForPlaybackAfterRebufferMs = 1000,
                    prioritizeTimeOverSize = true
                )
                NetworkType.CELLULAR_FAST -> BufferConfiguration(
                    minBufferMs = 1500,
                    maxBufferMs = 15000,
                    bufferForPlaybackMs = 1500,
                    bufferForPlaybackAfterRebufferMs = 1500,
                    prioritizeTimeOverSize = true
                )
                NetworkType.CELLULAR_SLOW -> BufferConfiguration(
                    minBufferMs = 3000,
                    maxBufferMs = 10000,
                    bufferForPlaybackMs = 3000,
                    bufferForPlaybackAfterRebufferMs = 2000,
                    prioritizeTimeOverSize = false
                )
                NetworkType.UNKNOWN -> BufferConfiguration(
                    minBufferMs = 2000,
                    maxBufferMs = 15000,
                    bufferForPlaybackMs = 2000,
                    bufferForPlaybackAfterRebufferMs = 1500,
                    prioritizeTimeOverSize = true
                )
            }
        }
    }
}

enum class NetworkType {
    WIFI,
    CELLULAR_FAST,
    CELLULAR_SLOW,
    UNKNOWN
}