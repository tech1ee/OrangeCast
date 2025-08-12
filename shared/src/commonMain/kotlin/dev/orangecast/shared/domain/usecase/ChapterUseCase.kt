package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.Chapter
import dev.orangecast.shared.domain.model.ChapterList
import dev.orangecast.shared.domain.repository.ChapterRepository
import dev.orangecast.shared.domain.repository.PlayerStateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ChapterUseCase(
    private val chapterRepository: ChapterRepository,
    private val playerStateRepository: PlayerStateRepository
) {

    suspend fun loadChaptersForCurrentEpisode(): ChapterList? {
        val currentState = playerStateRepository.getCurrentState()
        val currentTrack = currentState.currentTrack ?: return null
        
        // Check cache first
        val cachedChapters = chapterRepository.getCachedChapters(currentTrack.id)
        if (cachedChapters != null && cachedChapters.chapters.isNotEmpty()) {
            return cachedChapters
        }
        
        // Load chapters from audio metadata
        val chapters = chapterRepository.getChaptersForEpisode(currentTrack.id, currentTrack.url)
        
        // Cache the results
        if (chapters.chapters.isNotEmpty()) {
            chapterRepository.cacheChapters(currentTrack.id, chapters)
        }
        
        return chapters
    }

    suspend fun getCurrentChapter(): Chapter? {
        val currentState = playerStateRepository.getCurrentState()
        val chapters = loadChaptersForCurrentEpisode() ?: return null
        
        return chapters.getCurrentChapter(currentState.currentPosition)
    }

    suspend fun skipToNextChapter(): Boolean {
        val currentState = playerStateRepository.getCurrentState()
        val chapters = loadChaptersForCurrentEpisode() ?: return false
        
        val nextChapter = chapters.getNextChapter(currentState.currentPosition)
        if (nextChapter != null) {
            playerStateRepository.updatePosition(nextChapter.startTimeMs)
            return true
        }
        
        return false
    }

    suspend fun skipToPreviousChapter(): Boolean {
        val currentState = playerStateRepository.getCurrentState()
        val chapters = loadChaptersForCurrentEpisode() ?: return false
        
        val currentChapter = chapters.getCurrentChapter(currentState.currentPosition)
        
        // If we're more than 3 seconds into the current chapter, go to its beginning
        if (currentChapter != null && currentState.currentPosition - currentChapter.startTimeMs > 3000L) {
            playerStateRepository.updatePosition(currentChapter.startTimeMs)
            return true
        }
        
        // Otherwise, go to the previous chapter
        val previousChapter = chapters.getPreviousChapter(currentState.currentPosition)
        if (previousChapter != null) {
            playerStateRepository.updatePosition(previousChapter.startTimeMs)
            return true
        }
        
        return false
    }

    suspend fun skipToChapter(chapter: Chapter): Boolean {
        val chapters = loadChaptersForCurrentEpisode() ?: return false
        
        if (chapters.chapters.contains(chapter)) {
            playerStateRepository.updatePosition(chapter.startTimeMs)
            return true
        }
        
        return false
    }

    suspend fun skipToChapterIndex(index: Int): Boolean {
        val chapters = loadChaptersForCurrentEpisode() ?: return false
        val chapter = chapters.getChapterAtIndex(index) ?: return false
        
        return skipToChapter(chapter)
    }

    suspend fun getChapterProgress(): Float {
        val currentState = playerStateRepository.getCurrentState()
        val chapters = loadChaptersForCurrentEpisode() ?: return 0f
        
        return chapters.getChapterProgress(currentState.currentPosition)
    }

    suspend fun getChapterMarkers(): List<Float> {
        val chapters = loadChaptersForCurrentEpisode() ?: return emptyList()
        return chapters.getAllChapterMarkers()
    }

    suspend fun hasChapters(): Boolean {
        val chapters = loadChaptersForCurrentEpisode() ?: return false
        return chapters.chapters.isNotEmpty()
    }

    suspend fun getChapterCount(): Int {
        val chapters = loadChaptersForCurrentEpisode() ?: return 0
        return chapters.chapters.size
    }

    suspend fun getCurrentChapterIndex(): Int {
        val currentChapter = getCurrentChapter() ?: return -1
        val chapters = loadChaptersForCurrentEpisode() ?: return -1
        
        return chapters.getChapterIndex(currentChapter)
    }

    suspend fun getCurrentChapterInfo(): Triple<Int, Int, Chapter?> {
        val chapters = loadChaptersForCurrentEpisode()
        val currentChapter = getCurrentChapter()
        val currentIndex = if (currentChapter != null && chapters != null) {
            chapters.getChapterIndex(currentChapter)
        } else -1
        
        return Triple(
            currentIndex,
            chapters?.chapters?.size ?: 0,
            currentChapter
        )
    }

    fun observeCurrentChapter(): Flow<Chapter?> {
        return combine(
            playerStateRepository.observePlayerState(),
            // Would need to observe chapters as well, but for simplicity using current implementation
        ) { playerState, _ ->
            val chapters = loadChaptersForCurrentEpisode()
            chapters?.getCurrentChapter(playerState.currentPosition)
        }
    }

    suspend fun searchChapterByTitle(query: String): List<Chapter> {
        val chapters = loadChaptersForCurrentEpisode() ?: return emptyList()
        
        return chapters.chapters.filter { chapter ->
            chapter.title.contains(query, ignoreCase = true) ||
            chapter.description?.contains(query, ignoreCase = true) == true
        }
    }

    suspend fun getChapterAtPosition(positionMs: Long): Chapter? {
        val chapters = loadChaptersForCurrentEpisode() ?: return null
        return chapters.getCurrentChapter(positionMs)
    }

    suspend fun getNextChapterTitle(): String? {
        val currentState = playerStateRepository.getCurrentState()
        val chapters = loadChaptersForCurrentEpisode() ?: return null
        
        return chapters.getNextChapter(currentState.currentPosition)?.title
    }

    suspend fun clearChapterCache() {
        val currentState = playerStateRepository.getCurrentState()
        val currentTrack = currentState.currentTrack ?: return
        
        chapterRepository.clearChapterCache(currentTrack.id)
    }

    suspend fun refreshChapters(): ChapterList? {
        clearChapterCache()
        return loadChaptersForCurrentEpisode()
    }

    suspend fun hasNextChapter(): Boolean {
        val currentState = playerStateRepository.getCurrentState()
        val chapters = loadChaptersForCurrentEpisode() ?: return false
        
        return chapters.getNextChapter(currentState.currentPosition) != null
    }

    suspend fun hasPreviousChapter(): Boolean {
        val currentState = playerStateRepository.getCurrentState()
        val chapters = loadChaptersForCurrentEpisode() ?: return false
        
        val currentChapter = chapters.getCurrentChapter(currentState.currentPosition)
        
        // If we're more than 3 seconds into current chapter, we can go to its beginning
        if (currentChapter != null && currentState.currentPosition - currentChapter.startTimeMs > 3000L) {
            return true
        }
        
        return chapters.getPreviousChapter(currentState.currentPosition) != null
    }
}