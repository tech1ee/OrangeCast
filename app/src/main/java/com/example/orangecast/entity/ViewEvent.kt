package com.example.orangecast.entity

sealed class ViewEvent {
    data class Progress<T>(val inProgress: Boolean): ViewEvent()
    data class Error<T>(val message: String, val error: Throwable?): ViewEvent()
    data class Data<T>(val data: T): ViewEvent()
}