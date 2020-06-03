package com.example.orangecast.ui.library

import com.example.orangecast.entity.Subscriptions

sealed class LibraryViewEvent {
    data class Progress(val inProgress: Boolean): LibraryViewEvent()
    data class Error(val message: String): LibraryViewEvent()
    data class Data(val subscriptions: Subscriptions): LibraryViewEvent()
}