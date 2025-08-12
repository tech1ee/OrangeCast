package dev.orangecast.shared.domain.repository

import dev.orangecast.shared.domain.model.ChapterList
import dev.orangecast.shared.domain.model.ChapterMetadata

interface ChapterRepository {
    suspend fun getChaptersForEpisode(episodeId: String, audioUrl: String): ChapterList
    suspend fun parseChaptersFromMetadata(audioUrl: String): ChapterMetadata?
    suspend fun cacheChapters(episodeId: String, chapters: ChapterList)
    suspend fun getCachedChapters(episodeId: String): ChapterList?
    suspend fun clearChapterCache(episodeId: String)
}