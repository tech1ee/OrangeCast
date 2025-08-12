package dev.orangecast.shared.domain.model

import dev.orangecast.shared.domain.error.PodcastError

sealed class PodcastState<out T> {
    object Loading : PodcastState<Nothing>()
    data class Success<T>(val data: T) : PodcastState<T>()
    data class Error(val error: PodcastError) : PodcastState<Nothing>()
}