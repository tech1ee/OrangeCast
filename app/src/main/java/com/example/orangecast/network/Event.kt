package com.example.orangecast.network

sealed class Event<out T> {
    data class Progress(val inProgress: Boolean): Event<Nothing>()
    data class Success<T>(val data: T): Event<T>()
    data class Error(val message: String, val error: Throwable?): Event<Nothing>()
}