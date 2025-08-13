package dev.orangecast.shared.data.rss

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import dev.orangecast.shared.domain.model.PodcastEpisode
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class RssFeedParser(private val httpClient: HttpClient) {
    
    suspend fun parseEpisodes(feedUrl: String, podcastId: String, podcastTitle: String): Result<List<PodcastEpisode>> {
        return try {
            val feedContent: String = httpClient.get(feedUrl).body()
            val document: Document = Ksoup.parse(feedContent)
            
            val episodes = document.select("item").mapNotNull { item ->
                parseEpisodeItem(item, podcastId, podcastTitle)
            }
            
            Result.success(episodes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun parseEpisodeItem(item: Element, podcastId: String, podcastTitle: String): PodcastEpisode? {
        try {
            val title = item.selectFirst("title")?.text() ?: return null
            val description = item.selectFirst("description")?.text() ?: ""
            
            val enclosure = item.selectFirst("enclosure")
            val audioUrl = enclosure?.attr("url") ?: return null
            
            val pubDate = item.selectFirst("pubDate")?.text()
            val publishedAt = parsePubDate(pubDate) ?: Clock.System.now().toEpochMilliseconds()
            
            val durationElement = item.selectFirst("itunes:duration") 
                ?: item.selectFirst("duration")
            val duration = parseDuration(durationElement?.text())
            
            val thumbnailUrl = item.selectFirst("itunes:image")?.attr("href")
                ?: item.selectFirst("media:thumbnail")?.attr("url")
                ?: ""
            
            val isExplicit = item.selectFirst("itunes:explicit")?.text()
                ?.lowercase() in listOf("yes", "true", "explicit")
            
            val episodeNumber = item.selectFirst("itunes:episode")?.text()?.toIntOrNull()
            val seasonNumber = item.selectFirst("itunes:season")?.text()?.toIntOrNull()
            
            return PodcastEpisode(
                id = generateEpisodeId(audioUrl),
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
            val instant = Instant.parse(pubDateString)
            instant.toEpochMilliseconds()
        } catch (e: Exception) {
            try {
                val formats = listOf(
                    "EEE, dd MMM yyyy HH:mm:ss Z",
                    "EEE, dd MMM yyyy HH:mm:ss zzz",
                    "yyyy-MM-dd'T'HH:mm:ss'Z'",
                    "yyyy-MM-dd'T'HH:mm:ssZ"
                )
                
                Clock.System.now().toEpochMilliseconds()
            } catch (e: Exception) {
                null
            }
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