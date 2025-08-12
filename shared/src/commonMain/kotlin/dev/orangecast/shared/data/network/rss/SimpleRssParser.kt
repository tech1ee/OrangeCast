package dev.orangecast.shared.data.network.rss

import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.domain.model.PodcastDetails
import dev.orangecast.shared.domain.model.PodcastEpisode
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

class SimpleRssParser(private val httpClient: HttpClient) {
    
    suspend fun parseRssFeed(rssUrl: String): Result<PodcastFeed> {
        return try {
            val rssContent = httpClient.get(rssUrl).bodyAsText()
            
            val title = extractContent(rssContent, "<title>", "</title>") ?: "Unknown Podcast"
            val description = extractContent(rssContent, "<description>", "</description>") ?: ""
            val author = extractContent(rssContent, "<itunes:author>", "</itunes:author>") ?: "Unknown Author"
            
            val podcast = Podcast(
                id = title.hashCode().toString(),
                title = title.take(200),
                description = description.take(1000),
                imageUrl = "",
                author = author.take(100),
                categoryId = 0,
                categoryName = "",
                rssUrl = rssUrl,
                totalEpisodes = 10,
                latestEpisodeDate = Clock.System.now(),
                language = "en",
                isExplicit = false,
                website = ""
            )
            
            val episodes = parseSimpleEpisodes(rssContent, podcast.id, podcast.title)
            
            Result.success(
                PodcastFeed(
                    details = PodcastDetails(
                        podcast = podcast,
                        episodes = episodes
                    ),
                    episodes = episodes
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun parseSimpleEpisodes(content: String, podcastId: String, podcastTitle: String): List<PodcastEpisode> {
        val episodes = mutableListOf<PodcastEpisode>()
        val items = content.split("<item>").drop(1).take(50)
        
        for ((index, item) in items.withIndex()) {
            val title = extractContent(item, "<title>", "</title>") ?: "Episode $index"
            val description = extractContent(item, "<description>", "</description>") ?: ""
            val audioUrl = extractAudioUrl(item)
            
            episodes.add(
                PodcastEpisode(
                    id = "${podcastId}_$index",
                    title = title.take(200),
                    description = description.take(1000),
                    audioUrl = audioUrl,
                    imageUrl = null,
                    publishedAt = Clock.System.now(),
                    duration = 1800,
                    podcastId = podcastId,
                    podcastTitle = podcastTitle,
                    isExplicit = false,
                    episodeNumber = index + 1,
                    seasonNumber = 1
                )
            )
        }
        
        return episodes
    }
    
    private fun extractContent(content: String, startTag: String, endTag: String): String? {
        val start = content.indexOf(startTag, ignoreCase = true)
        if (start == -1) return null
        
        val contentStart = start + startTag.length
        val end = content.indexOf(endTag, contentStart, ignoreCase = true)
        if (end == -1) return null
        
        return content.substring(contentStart, end).trim()
            .replace(Regex("<[^>]+>"), "")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
    }
    
    private fun extractAudioUrl(content: String): String {
        val enclosureStart = content.indexOf("<enclosure", ignoreCase = true)
        if (enclosureStart == -1) return ""
        
        val enclosureEnd = content.indexOf(">", enclosureStart)
        if (enclosureEnd == -1) return ""
        
        val enclosureTag = content.substring(enclosureStart, enclosureEnd + 1)
        
        val urlMatch = Regex("url=\"([^\"]+)\"").find(enclosureTag)
        return urlMatch?.groupValues?.get(1) ?: ""
    }
}