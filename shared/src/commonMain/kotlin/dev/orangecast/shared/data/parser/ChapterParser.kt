package dev.orangecast.shared.data.parser

import dev.orangecast.shared.domain.model.Chapter
import dev.orangecast.shared.domain.model.ChapterInfo
import dev.orangecast.shared.domain.model.ChapterList
import dev.orangecast.shared.domain.model.ChapterMetadata

expect class ChapterParser() {
    suspend fun parseChaptersFromAudio(audioUrl: String): ChapterMetadata?
    suspend fun extractID3v2Chapters(audioUrl: String): List<ChapterInfo>
    suspend fun extractMP4Chapters(audioUrl: String): List<ChapterInfo>
}

class CommonChapterParser {
    
    fun convertChapterInfoToChapters(
        episodeId: String,
        chapterInfos: List<ChapterInfo>,
        totalDurationMs: Long
    ): ChapterList {
        if (chapterInfos.isEmpty()) {
            return ChapterList.empty(episodeId)
        }
        
        val sortedChapterInfos = chapterInfos.sortedBy { it.parseStartTimeMs() }
        val chapters = mutableListOf<Chapter>()
        
        for (i in sortedChapterInfos.indices) {
            val chapterInfo = sortedChapterInfos[i]
            val startTimeMs = chapterInfo.parseStartTimeMs()
            val endTimeMs = if (i < sortedChapterInfos.size - 1) {
                sortedChapterInfos[i + 1].parseStartTimeMs()
            } else {
                totalDurationMs
            }
            
            if (startTimeMs < totalDurationMs && endTimeMs > startTimeMs) {
                val chapter = Chapter(
                    id = "${episodeId}_chapter_$i",
                    title = chapterInfo.title,
                    startTimeMs = startTimeMs,
                    endTimeMs = endTimeMs.coerceAtMost(totalDurationMs),
                    artworkUrl = null, // Will be processed separately
                    description = chapterInfo.description,
                    url = chapterInfo.url
                )
                chapters.add(chapter)
            }
        }
        
        return ChapterList(
            episodeId = episodeId,
            chapters = chapters,
            totalDurationMs = totalDurationMs
        )
    }
    
    fun validateChapterTiming(chapters: List<ChapterInfo>): List<ChapterInfo> {
        val sortedChapters = chapters.sortedBy { it.parseStartTimeMs() }
        val validatedChapters = mutableListOf<ChapterInfo>()
        
        for (i in sortedChapters.indices) {
            val chapter = sortedChapters[i]
            val startTimeMs = chapter.parseStartTimeMs()
            
            // Skip chapters with invalid start times
            if (startTimeMs < 0) continue
            
            // Skip chapters that start after the previous chapter's theoretical end
            if (validatedChapters.isNotEmpty()) {
                val lastChapter = validatedChapters.last()
                if (startTimeMs <= lastChapter.parseStartTimeMs()) continue
            }
            
            validatedChapters.add(chapter)
        }
        
        return validatedChapters
    }
    
    fun generateAutomaticChapters(
        episodeId: String,
        totalDurationMs: Long,
        intervalMinutes: Int = 10
    ): ChapterList {
        if (totalDurationMs <= 0 || intervalMinutes <= 0) {
            return ChapterList.empty(episodeId)
        }
        
        val intervalMs = intervalMinutes * 60 * 1000L
        val chapters = mutableListOf<Chapter>()
        
        var currentTime = 0L
        var chapterNumber = 1
        
        while (currentTime < totalDurationMs) {
            val endTime = (currentTime + intervalMs).coerceAtMost(totalDurationMs)
            
            val chapter = Chapter(
                id = "${episodeId}_auto_chapter_$chapterNumber",
                title = "Chapter $chapterNumber",
                startTimeMs = currentTime,
                endTimeMs = endTime,
                description = "${Chapter.formatTimeMs(currentTime)} - ${Chapter.formatTimeMs(endTime)}"
            )
            
            chapters.add(chapter)
            currentTime = endTime
            chapterNumber++
        }
        
        return ChapterList(
            episodeId = episodeId,
            chapters = chapters,
            totalDurationMs = totalDurationMs
        )
    }
    
    fun mergeOverlappingChapters(chapters: List<Chapter>): List<Chapter> {
        if (chapters.size <= 1) return chapters
        
        val sortedChapters = chapters.sortedBy { it.startTimeMs }
        val mergedChapters = mutableListOf<Chapter>()
        
        var currentChapter = sortedChapters.first()
        
        for (i in 1 until sortedChapters.size) {
            val nextChapter = sortedChapters[i]
            
            if (currentChapter.endTimeMs >= nextChapter.startTimeMs) {
                // Merge overlapping chapters
                currentChapter = currentChapter.copy(
                    title = "${currentChapter.title} / ${nextChapter.title}",
                    endTimeMs = maxOf(currentChapter.endTimeMs, nextChapter.endTimeMs),
                    description = listOfNotNull(
                        currentChapter.description, 
                        nextChapter.description
                    ).joinToString(" | ").takeIf { it.isNotEmpty() }
                )
            } else {
                mergedChapters.add(currentChapter)
                currentChapter = nextChapter
            }
        }
        
        mergedChapters.add(currentChapter)
        return mergedChapters
    }
    
    fun extractChapterFromPodcastDescription(description: String): List<ChapterInfo> {
        val chapters = mutableListOf<ChapterInfo>()
        val timeRegex = Regex("""(\d{1,2}):(\d{2})(?::(\d{2}))?""")
        val lines = description.lines()
        
        for (line in lines) {
            val match = timeRegex.find(line)
            if (match != null) {
                val timeString = match.value
                val title = line.replace(match.value, "").trim()
                    .removePrefix("-").removePrefix("*").removePrefix("•").trim()
                
                if (title.isNotEmpty()) {
                    chapters.add(
                        ChapterInfo(
                            startTime = timeString,
                            title = title,
                            description = line.trim()
                        )
                    )
                }
            }
        }
        
        return chapters
    }
}