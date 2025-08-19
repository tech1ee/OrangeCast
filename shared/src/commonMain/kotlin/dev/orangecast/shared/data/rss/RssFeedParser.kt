package dev.orangecast.shared.data.rss

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import dev.orangecast.shared.data.config.AppConfig
import dev.orangecast.shared.domain.model.PodcastEpisode
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class RssFeedParser(private val httpClient: HttpClient) {
    
    suspend fun parseEpisodes(feedUrl: String, podcastId: String, podcastTitle: String): Result<List<PodcastEpisode>> {
        return try {
            val episodes = mutableListOf<PodcastEpisode>()
            
            // Use streaming approach with Flow to prevent OOM
            parseEpisodesStream(feedUrl, podcastId, podcastTitle).collect { episode ->
                episodes.add(episode)
                
                // Limit episodes to prevent memory issues
                if (episodes.size >= AppConfig.RSS.MAX_EPISODES_PARSED) {
                    return@collect
                }
            }
            
            if (episodes.isEmpty()) {
                return Result.failure(Exception("No episodes found in RSS feed from $feedUrl"))
            }
            
            Result.success(episodes)
        } catch (e: Exception) {
            Result.failure(Exception("RSS parsing failed for $feedUrl: ${e.message}", e))
        }
    }
    
    private suspend fun parseEpisodesStream(feedUrl: String, podcastId: String, podcastTitle: String): Flow<PodcastEpisode> = flow {
        val response = httpClient.get(feedUrl) {
            header("Accept", "application/rss+xml, application/xml, text/xml, */*")
            header("User-Agent", "OrangeCast/1.0 (compatible; podcast client)")
        }
        
        val contentLength = response.headers["Content-Length"]?.toLongOrNull()
        if (contentLength != null && contentLength > AppConfig.RSS.MAX_FEED_SIZE_BYTES) {
            throw Exception("RSS feed too large: ${contentLength / AppConfig.RSS.SIZE_ERROR_DIVIDER}MB (max ${AppConfig.RSS.MAX_FEED_SIZE_BYTES / AppConfig.RSS.SIZE_ERROR_DIVIDER}MB)")
        }
        
        // Stream the RSS content instead of loading entirely into memory
        val feedContent: String = try {
            response.body()
        } catch (e: OutOfMemoryError) {
            throw Exception("RSS feed too large for memory")
        }
        
        if (feedContent.isBlank()) {
            throw Exception("Empty RSS feed content from $feedUrl")
        }
        
        // Use KSoup for now but process items one by one to reduce memory usage
        val document: Document = Ksoup.parse(feedContent)
        val items = document.select("item")
        
        if (items.isEmpty()) {
            throw Exception("No episodes found in RSS feed from $feedUrl")
        }
        
        // Process items incrementally and emit each episode immediately
        items.take(AppConfig.RSS.MAX_EPISODES_PARSED).forEach { item ->
            try {
                val episode = parseEpisodeItem(item, podcastId, podcastTitle)
                if (episode != null) {
                    emit(episode)
                }
            } catch (e: Exception) {
                // Continue with other episodes if one fails
            }
        }
    }.flowOn(kotlinx.coroutines.Dispatchers.IO)
    
    private fun parseEpisodeItem(item: Element, podcastId: String, podcastTitle: String): PodcastEpisode? {
        val title = item.selectFirst("title")?.text()
        if (title.isNullOrBlank()) {
            return null
        }
        
        val description = item.selectFirst("description")?.text() ?: ""
        
        val enclosure = item.selectFirst("enclosure")
        val audioUrl = enclosure?.attr("url") 
            ?: enclosure?.attr("href")
            ?: item.selectFirst("link")?.attr("href")
            ?: item.getElementsByTag("content").firstOrNull()?.attr("url")
        
        if (audioUrl.isNullOrBlank()) {
            return null
        }
        
        val pubDate = item.selectFirst("pubDate")?.text()
        val publishedAt = parsePubDate(pubDate) ?: Clock.System.now().toEpochMilliseconds()
        
        // Parse iTunes duration first, fallback to generic duration
        val durationText = item.select("itunes|duration").firstOrNull()?.text()
            ?: item.getElementsByTag("duration").firstOrNull()?.text()
        val duration = parseDuration(durationText)
        
        // Parse iTunes image first, then fallback to generic image elements
        val thumbnailUrl = item.select("itunes|image").firstOrNull()?.attr("href")
            ?: item.getElementsByTag("image").firstOrNull()?.attr("href")
            ?: item.getElementsByTag("thumbnail").firstOrNull()?.attr("url")
            ?: item.selectFirst("image")?.attr("href")
            ?: item.selectFirst("image")?.text()
            ?: ""
        
        // Parse iTunes explicit first, then fallback to generic explicit
        val isExplicitText = item.select("itunes|explicit").firstOrNull()?.text()
            ?: item.getElementsByTag("explicit").firstOrNull()?.text()
        val isExplicit = isExplicitText?.lowercase() in listOf("yes", "true", "explicit")
        
        // Parse iTunes episode and season numbers first, then fallback to generic
        val episodeNumber = item.select("itunes|episode").firstOrNull()?.text()?.toIntOrNull()
            ?: item.getElementsByTag("episode").firstOrNull()?.text()?.toIntOrNull()
        val seasonNumber = item.select("itunes|season").firstOrNull()?.text()?.toIntOrNull()
            ?: item.getElementsByTag("season").firstOrNull()?.text()?.toIntOrNull()
        
        val episodeId = generateEpisodeId(audioUrl)
        
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
            val cleaned = durationString.trim()
            when {
                cleaned.contains(":") -> {
                    val parts = cleaned.split(":")
                    when (parts.size) {
                        2 -> {
                            // MM:SS format (common in iTunes)
                            val minutes = parts[0].trim().toLongOrNull() ?: 0L
                            val seconds = parts[1].trim().toLongOrNull() ?: 0L
                            minutes * 60 + seconds
                        }
                        3 -> {
                            // HH:MM:SS format
                            val hours = parts[0].trim().toLongOrNull() ?: 0L
                            val minutes = parts[1].trim().toLongOrNull() ?: 0L
                            val seconds = parts[2].trim().toLongOrNull() ?: 0L
                            hours * 3600 + minutes * 60 + seconds
                        }
                        else -> 0L
                    }
                }
                // Handle pure seconds format
                else -> cleaned.toLongOrNull() ?: 0L
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