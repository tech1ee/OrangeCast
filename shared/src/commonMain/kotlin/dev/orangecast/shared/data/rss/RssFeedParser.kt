package dev.orangecast.shared.data.rss

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import dev.orangecast.shared.domain.model.PodcastEpisode
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class RssFeedParser(private val httpClient: HttpClient) {
    
    suspend fun parseEpisodes(feedUrl: String, podcastId: String, podcastTitle: String): Result<List<PodcastEpisode>> {
        return try {
            val feedContent: String = httpClient.get(feedUrl) {
                // Override default JSON Accept header for RSS feeds
                header("Accept", "application/rss+xml, application/xml, text/xml, */*")
                header("User-Agent", "OrangeCast/1.0 (compatible; podcast client)")
            }.body()
            
            if (feedContent.isBlank()) {
                return Result.failure(Exception("Empty RSS feed content from $feedUrl"))
            }
            
            val document: Document = Ksoup.parse(feedContent)
            val items = document.select("item")
            
            if (items.isEmpty()) {
                return Result.failure(Exception("No episodes found in RSS feed from $feedUrl"))
            }
            
            val episodes = items.mapNotNull { item ->
                parseEpisodeItem(item, podcastId, podcastTitle)
            }
            
            if (episodes.isEmpty()) {
                return Result.failure(Exception("Failed to parse any episodes from ${items.size} items in RSS feed"))
            }
            
            Result.success(episodes)
        } catch (e: Exception) {
            Result.failure(Exception("RSS parsing failed for $feedUrl: ${e.message}", e))
        }
    }
    
    private fun parseEpisodeItem(item: Element, podcastId: String, podcastTitle: String): PodcastEpisode? {
        try {
            val title = item.selectFirst("title")?.text()
            if (title.isNullOrBlank()) {
                return null
            }
            
            val description = item.selectFirst("description")?.text() ?: ""
            
            // Try multiple ways to find the audio URL - now more flexible
            val enclosure = item.selectFirst("enclosure")
            val audioUrl = enclosure?.attr("url") 
                ?: enclosure?.attr("href")
                ?: item.selectFirst("link")?.attr("href")
                ?: item.getElementsByTag("content").firstOrNull()?.attr("url")
                ?: "" // Allow empty audio URL
            
            // Allow episodes without audio URLs (for text episodes, transcripts, etc.)
            
            val pubDate = item.selectFirst("pubDate")?.text()
            val publishedAt = parsePubDate(pubDate) ?: Clock.System.now().toEpochMilliseconds()
            
            // Fix namespace-prefixed element selection for iTunes tags
            val durationElement = item.getElementsByTag("duration").firstOrNull()
            val duration = parseDuration(durationElement?.text())
            
            val thumbnailUrl = item.getElementsByTag("image").firstOrNull()?.attr("href")
                ?: item.getElementsByTag("thumbnail").firstOrNull()?.attr("url")
                ?: item.selectFirst("image")?.attr("href")
                ?: item.selectFirst("image")?.text()
                ?: ""
            
            val isExplicit = item.getElementsByTag("explicit").firstOrNull()?.text()
                ?.lowercase() in listOf("yes", "true", "explicit")
            
            val episodeNumber = item.getElementsByTag("episode").firstOrNull()?.text()?.toIntOrNull()
            val seasonNumber = item.getElementsByTag("season").firstOrNull()?.text()?.toIntOrNull()
            
            // Create episode even without audio URL (for text episodes, transcripts, etc.)
            val episodeId = if (audioUrl.isNotBlank()) {
                generateEpisodeId(audioUrl)
            } else {
                generateEpisodeId("$podcastId-$title-$publishedAt")
            }
            
            return PodcastEpisode(
                id = episodeId,
                title = title,
                description = cleanDescription(description),
                audioUrl = audioUrl,
                thumbnailUrl = thumbnailUrl,
                publishedAt = publishedAt,
                duration = duration,
                podcastId = podcastId,
                podcastTitle = podcastTitle,
                isExplicit = isExplicit,
                episodeNumber = episodeNumber,
                seasonNumber = seasonNumber
            )
        } catch (e: Exception) {
            return null
        }
    }
    
    private fun parsePubDate(pubDateString: String?): Long? {
        if (pubDateString.isNullOrBlank()) return null
        
        return try {
            // Try ISO format first
            val instant = Instant.parse(pubDateString)
            instant.toEpochMilliseconds()
        } catch (e: Exception) {
            try {
                // Try RFC 2822 format common in RSS feeds
                val cleaned = pubDateString.trim()
                    .replace(Regex("\\s+"), " ")
                    .replace(" GMT", " +0000")
                    .replace(" UTC", " +0000")
                    .replace(" EST", " -0500")
                    .replace(" PST", " -0800")
                    .replace(" MST", " -0700")
                    .replace(" CST", " -0600")
                
                // Simple parsing for common RSS date patterns
                when {
                    cleaned.matches(Regex("\\w{3}, \\d{1,2} \\w{3} \\d{4} \\d{2}:\\d{2}:\\d{2}.*")) -> {
                        // Extract date components and create a reasonable timestamp
                        val parts = cleaned.split(" ")
                        if (parts.size >= 4) {
                            val year = parts[3].toIntOrNull() ?: 2024
                            val month = getMonthNumber(parts[2])
                            val day = parts[1].toIntOrNull() ?: 1
                            
                            // Create approximate timestamp (not perfect but better than current time)
                            val baseYear = 1970
                            val approximateTimestamp = ((year - baseYear) * 365L + month * 30L + day) * 24L * 60L * 60L * 1000L
                            approximateTimestamp
                        } else {
                            null
                        }
                    }
                    else -> null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    private fun getMonthNumber(monthName: String): Int {
        return when (monthName.lowercase()) {
            "jan" -> 1
            "feb" -> 2
            "mar" -> 3
            "apr" -> 4
            "may" -> 5
            "jun" -> 6
            "jul" -> 7
            "aug" -> 8
            "sep" -> 9
            "oct" -> 10
            "nov" -> 11
            "dec" -> 12
            else -> 1
        }
    }
    
    private fun parseDuration(durationString: String?): Long {
        if (durationString.isNullOrBlank()) return 0L
        
        return try {
            when {
                durationString.contains(":") -> {
                    val parts = durationString.split(":")
                    when (parts.size) {
                        2 -> {
                            val minutes = parts[0].toLongOrNull() ?: 0L
                            val seconds = parts[1].toLongOrNull() ?: 0L
                            minutes * 60 + seconds
                        }
                        3 -> {
                            val hours = parts[0].toLongOrNull() ?: 0L
                            val minutes = parts[1].toLongOrNull() ?: 0L
                            val seconds = parts[2].toLongOrNull() ?: 0L
                            hours * 3600 + minutes * 60 + seconds
                        }
                        else -> 0L
                    }
                }
                else -> durationString.toLongOrNull() ?: 0L
            }
        } catch (e: Exception) {
            0L
        }
    }
    
    private fun generateEpisodeId(audioUrl: String): String {
        return audioUrl.hashCode().toString()
    }
    
    private fun cleanDescription(description: String): String {
        return Ksoup.parse(description).text()
    }
}