package dev.orangecast.shared.data.config

interface ApiKeyProvider {
    fun getListenNotesApiKey(): String
    fun getPodcastIndexApiKey(): String
    fun getPodcastIndexApiSecret(): String
}