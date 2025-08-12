package dev.orangecast.shared.domain.model

data class Chapter(
    val id: String,
    val title: String,
    val startTimeMs: Long,
    val endTimeMs: Long,
    val artworkUrl: String? = null,
    val description: String? = null,
    val url: String? = null
) {
    val durationMs: Long = endTimeMs - startTimeMs
    
    fun contains(positionMs: Long): Boolean {
        return positionMs >= startTimeMs && positionMs < endTimeMs
    }
    
    fun formatStartTime(): String {
        return formatTimeMs(startTimeMs)
    }
    
    fun formatDuration(): String {
        return formatTimeMs(durationMs)
    }
    
    companion object {
        fun formatTimeMs(timeMs: Long): String {
            val totalSeconds = (timeMs / 1000).toInt()
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            
            return if (hours > 0) {
                String.format("%d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%d:%02d", minutes, seconds)
            }
        }
    }
}

data class ChapterList(
    val episodeId: String,
    val chapters: List<Chapter>,
    val totalDurationMs: Long
) {
    fun getCurrentChapter(positionMs: Long): Chapter? {
        return chapters.find { it.contains(positionMs) }
    }
    
    fun getNextChapter(currentPositionMs: Long): Chapter? {
        return chapters.find { it.startTimeMs > currentPositionMs }
    }
    
    fun getPreviousChapter(currentPositionMs: Long): Chapter? {
        return chapters.reversed().find { it.startTimeMs < currentPositionMs }
    }
    
    fun getChapterAtIndex(index: Int): Chapter? {
        return chapters.getOrNull(index)
    }
    
    fun getChapterIndex(chapter: Chapter): Int {
        return chapters.indexOf(chapter)
    }
    
    fun getChapterProgress(positionMs: Long): Float {
        val currentChapter = getCurrentChapter(positionMs) ?: return 0f
        val chapterPosition = positionMs - currentChapter.startTimeMs
        return if (currentChapter.durationMs > 0) {
            (chapterPosition.toFloat() / currentChapter.durationMs.toFloat()).coerceIn(0f, 1f)
        } else 0f
    }
    
    fun getAllChapterMarkers(): List<Float> {
        return if (totalDurationMs > 0) {
            chapters.map { (it.startTimeMs.toFloat() / totalDurationMs.toFloat()) }
        } else emptyList()
    }
    
    companion object {
        fun empty(episodeId: String): ChapterList {
            return ChapterList(
                episodeId = episodeId,
                chapters = emptyList(),
                totalDurationMs = 0L
            )
        }
    }
}

data class ChapterMetadata(
    val title: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val chapters: List<ChapterInfo> = emptyList()
)

data class ChapterInfo(
    val startTime: String, // Format: "HH:MM:SS.mmm" or "MM:SS.mmm"
    val title: String,
    val artworkData: ByteArray? = null,
    val url: String? = null,
    val description: String? = null
) {
    fun parseStartTimeMs(): Long {
        return try {
            parseTimeStringToMs(startTime)
        } catch (e: Exception) {
            0L
        }
    }
    
    private fun parseTimeStringToMs(timeString: String): Long {
        val parts = timeString.split(":")
        val milliseconds = if (parts.last().contains(".")) {
            val secParts = parts.last().split(".")
            val seconds = secParts[0].toLong()
            val ms = secParts[1].padEnd(3, '0').take(3).toLong()
            seconds * 1000 + ms
        } else {
            parts.last().toLong() * 1000
        }
        
        return when (parts.size) {
            3 -> { // HH:MM:SS or HH:MM:SS.mmm
                val hours = parts[0].toLong()
                val minutes = parts[1].toLong()
                hours * 3600000 + minutes * 60000 + milliseconds
            }
            2 -> { // MM:SS or MM:SS.mmm
                val minutes = parts[0].toLong()
                minutes * 60000 + milliseconds
            }
            1 -> milliseconds // SS or SS.mmm
            else -> throw IllegalArgumentException("Invalid time format: $timeString")
        }
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChapterInfo) return false
        
        if (startTime != other.startTime) return false
        if (title != other.title) return false
        if (url != other.url) return false
        if (description != other.description) return false
        if (artworkData != null) {
            if (other.artworkData == null) return false
            if (!artworkData.contentEquals(other.artworkData)) return false
        } else if (other.artworkData != null) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = startTime.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (artworkData?.contentHashCode() ?: 0)
        return result
    }
}