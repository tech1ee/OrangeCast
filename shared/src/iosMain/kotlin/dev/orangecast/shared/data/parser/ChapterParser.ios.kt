package dev.orangecast.shared.data.parser

import dev.orangecast.shared.domain.model.ChapterInfo
import dev.orangecast.shared.domain.model.ChapterMetadata
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.AVFoundation.*
import platform.Foundation.*

@OptIn(ExperimentalForeignApi::class)
actual class ChapterParser {
    
    actual suspend fun parseChaptersFromAudio(audioUrl: String): ChapterMetadata? {
        return withContext(Dispatchers.Default) {
            try {
                val url = NSURL.URLWithString(audioUrl) ?: return@withContext null
                val asset = AVAsset.assetWithURL(url)
                
                // Load asset metadata
                val metadataItems = asset.commonMetadata
                
                var title: String? = null
                var artist: String? = null
                var album: String? = null
                
                for (item in metadataItems) {
                    val metadataItem = item as AVMetadataItem
                    when (metadataItem.commonKey) {
                        AVMetadataCommonKeyTitle -> title = metadataItem.stringValue
                        AVMetadataCommonKeyArtist -> artist = metadataItem.stringValue
                        AVMetadataCommonKeyAlbumName -> album = metadataItem.stringValue
                    }
                }
                
                // Extract chapters
                val chapters = when {
                    audioUrl.contains(".mp3", ignoreCase = true) -> extractID3v2Chapters(audioUrl)
                    audioUrl.contains(".m4a", ignoreCase = true) || 
                    audioUrl.contains(".mp4", ignoreCase = true) -> extractMP4Chapters(audioUrl)
                    else -> emptyList()
                }
                
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
        return withContext(Dispatchers.Default) {
            try {
                val url = NSURL.URLWithString(audioUrl) ?: return@withContext emptyList()
                val asset = AVAsset.assetWithURL(url)
                
                // Look for ID3 chapter metadata
                val id3Metadata = asset.metadataForFormat(AVMetadataFormatID3Metadata)
                val chapters = mutableListOf<ChapterInfo>()
                
                for (item in id3Metadata) {
                    val metadataItem = item as AVMetadataItem
                    
                    // Look for chapter-related metadata
                    if (metadataItem.identifier?.contains("CHAP") == true) {
                        val chapterInfo = parseID3ChapterItem(metadataItem)
                        if (chapterInfo != null) {
                            chapters.add(chapterInfo)
                        }
                    }
                }
                
                chapters.sortedBy { it.parseStartTimeMs() }
                
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    actual suspend fun extractMP4Chapters(audioUrl: String): List<ChapterInfo> {
        return withContext(Dispatchers.Default) {
            try {
                val url = NSURL.URLWithString(audioUrl) ?: return@withContext emptyList()
                val asset = AVAsset.assetWithURL(url)
                
                // Extract chapter track from MP4
                val chapterTracks = asset.tracksWithMediaType(AVMediaTypeText)
                val chapters = mutableListOf<ChapterInfo>()
                
                for (track in chapterTracks) {
                    val assetTrack = track as AVAssetTrack
                    val chapterItems = extractChaptersFromTrack(assetTrack)
                    chapters.addAll(chapterItems)
                }
                
                // Also check for timed metadata tracks
                val timedMetadataTracks = asset.tracksWithMediaType(AVMediaTypeTimedMetadata)
                for (track in timedMetadataTracks) {
                    val assetTrack = track as AVAssetTrack
                    val timedChapters = extractTimedMetadataChapters(assetTrack)
                    chapters.addAll(timedChapters)
                }
                
                chapters.sortedBy { it.parseStartTimeMs() }
                
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    private fun parseID3ChapterItem(metadataItem: AVMetadataItem): ChapterInfo? {
        return try {
            val value = metadataItem.stringValue ?: return null
            val parts = value.split("|")
            
            if (parts.size >= 2) {
                val timeString = parts[0].trim()
                val title = parts[1].trim()
                val description = if (parts.size > 2) parts[2].trim() else null
                
                ChapterInfo(
                    startTime = timeString,
                    title = title,
                    description = description
                )
            } else null
            
        } catch (e: Exception) {
            null
        }
    }
    
    private fun extractChaptersFromTrack(track: AVAssetTrack): List<ChapterInfo> {
        val chapters = mutableListOf<ChapterInfo>()
        
        try {
            // This would require more detailed implementation
            // For now, return empty list as it requires complex AVFoundation usage
            
        } catch (e: Exception) {
            // Handle parsing errors
        }
        
        return chapters
    }
    
    private fun extractTimedMetadataChapters(track: AVAssetTrack): List<ChapterInfo> {
        val chapters = mutableListOf<ChapterInfo>()
        
        try {
            // Extract timed metadata that might contain chapter information
            val formatDescriptions = track.formatDescriptions
            
            for (formatDesc in formatDescriptions) {
                // Parse format description for chapter data
                // This requires detailed CoreMedia framework usage
            }
            
        } catch (e: Exception) {
            // Handle parsing errors
        }
        
        return chapters
    }
    
    private fun timeStringFromCMTime(time: platform.CoreMedia.CMTime): String {
        val seconds = platform.CoreMedia.CMTimeGetSeconds(time)
        val totalSecs = seconds.toInt()
        val hours = totalSecs / 3600
        val mins = (totalSecs % 3600) / 60
        val secs = totalSecs % 60
        val ms = ((seconds - totalSecs) * 1000).toInt()
        
        return if (hours > 0) {
            String.format("%d:%02d:%02d.%03d", hours, mins, secs, ms)
        } else {
            String.format("%d:%02d.%03d", mins, secs, ms)
        }
    }
}