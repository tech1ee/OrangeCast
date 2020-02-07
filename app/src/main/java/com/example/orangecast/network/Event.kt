package com.example.orangecast.network

sealed class Event {
    data class Progress(val inProgress: Boolean): Event()
    data class Error(val message: String, val error: Throwable?): Event()
    data class Data<T>(val data: T): Event()
}