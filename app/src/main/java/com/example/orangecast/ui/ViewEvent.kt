package com.example.orangecast.ui

sealed class ViewEvent {
    data class Progress(val inProgress: Boolean): ViewEvent()
    data class Error(val message: String, val error: Throwable?): ViewEvent()
    data class Data<T>(val data: T): ViewEvent()
}