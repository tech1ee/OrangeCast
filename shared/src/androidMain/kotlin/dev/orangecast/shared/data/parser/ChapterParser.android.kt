package dev.orangecast.shared.data.parser

import android.media.MediaMetadataRetriever
import dev.orangecast.shared.domain.model.ChapterInfo
import dev.orangecast.shared.domain.model.ChapterMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual class ChapterParser {
    
    actual suspend fun parseChaptersFromAudio(audioUrl: String): ChapterMetadata? {
        return withContext(Dispatchers.IO) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(audioUrl)
                
                val chapters = when {
                    isMP3(audioUrl) -> extractID3v2Chapters(audioUrl)
                    isMP4(audioUrl) -> extractMP4Chapters(audioUrl)
                    else -> emptyList()
                }
                
                val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                
                retriever.release()
                
                if (chapters.isNotEmpty()) {
                    ChapterMetadata(
                        title = title,
                        artist = artist,
                        album = album,
                        chapters = chapters
                    )
                } else null
                
            } catch (e: Exception) {
                null
            }
        }
    }
    
    actual suspend fun extractID3v2Chapters(audioUrl: String): List<ChapterInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(audioUrl)
                
                // Try to extract embedded cue sheet or chapter information
                val chapterData = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) // Fallback approach
                
                retriever.release()
                
                if (chapterData != null) {
                    parseChapterDataFromID3(chapterData)
                } else {
                    emptyList()
                }
                
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    actual suspend fun extractMP4Chapters(audioUrl: String): List<ChapterInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(audioUrl)
                
                // MP4 chapter extraction would require more complex parsing
                // For now, return empty list as this requires native implementation
                val chapters = mutableListOf<ChapterInfo>()
                
                retriever.release()
                chapters
                
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    private fun isMP3(url: String): Boolean {
        return url.contains(".mp3", ignoreCase = true) || 
               url.contains("audio/mpeg", ignoreCase = true)
    }
    
    private fun isMP4(url: String): Boolean {
        return url.contains(".m4a", ignoreCase = true) || 
               url.contains(".mp4", ignoreCase = true) ||
               url.contains("audio/mp4", ignoreCase = true) ||
               url.contains("audio/aac", ignoreCase = true)
    }
    
    private fun parseChapterDataFromID3(chapterData: String): List<ChapterInfo> {
        val chapters = mutableListOf<ChapterInfo>()
        
        // Simple parsing approach for chapter data
        // This would need to be enhanced for production use
        val lines = chapterData.split('\n')
        
        for (line in lines) {
            val parts = line.split('|')
            if (parts.size >= 2) {
                val timeString = parts[0].trim()
                val title = parts[1].trim()
                
                if (isValidTimeString(timeString) && title.isNotEmpty()) {
                    chapters.add(
                        ChapterInfo(
                            startTime = timeString,
                            title = title,
                            description = if (parts.size > 2) parts[2].trim() else null
                        )
                    )
                }
            }
        }
        
        return chapters
    }
    
    private fun isValidTimeString(timeString: String): Boolean {
        val timePattern = Regex("""^\d{1,2}:\d{2}(:\d{2})?(\.\d{1,3})?$""")
        return timePattern.matches(timeString)
    }
    
    private fun parseTimeToMilliseconds(timeString: String): Long {
        try {
            val parts = timeString.split(':')
            var milliseconds = 0L
            
            when (parts.size) {
                3 -> { // HH:MM:SS or HH:MM:SS.mmm
                    val hours = parts[0].toLong()
                    val minutes = parts[1].toLong()
                    val secondsPart = parts[2]
                    
                    val seconds = if (secondsPart.contains('.')) {
                        val secParts = secondsPart.split('.')
                        val secs = secParts[0].toLong()
                        val ms = secParts[1].padEnd(3, '0').take(3).toLong()
                        secs * 1000 + ms
                    } else {
                        secondsPart.toLong() * 1000
                    }
                    
                    milliseconds = hours * 3600000 + minutes * 60000 + seconds
                }
                2 -> { // MM:SS or MM:SS.mmm
                    val minutes = parts[0].toLong()
                    val secondsPart = parts[1]
                    
                    val seconds = if (secondsPart.contains('.')) {
                        val secParts = secondsPart.split('.')
                        val secs = secParts[0].toLong()
                        val ms = secParts[1].padEnd(3, '0').take(3).toLong()
                        secs * 1000 + ms
                    } else {
                        secondsPart.toLong() * 1000
                    }
                    
                    milliseconds = minutes * 60000 + seconds
                }
            }
            
            return milliseconds
        } catch (e: Exception) {
            return 0L
        }
    }
}