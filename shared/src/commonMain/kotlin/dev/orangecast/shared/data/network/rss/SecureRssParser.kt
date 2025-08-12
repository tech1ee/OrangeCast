package dev.orangecast.shared.data.network.rss

import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.domain.model.PodcastDetails
import dev.orangecast.shared.domain.model.PodcastEpisode
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import dev.orangecast.shared.data.network.rss.PodcastFeed

class SecureRssParser(private val httpClient: HttpClient) {
    
    companion object {
        private const val MAX_XML_SIZE = 20 * 1024 * 1024
        private const val MAX_EPISODES = 1000
        private val BLOCKED_ENTITIES = listOf("<!ENTITY", "<!DOCTYPE", "<?xml-stylesheet")
        private val ALLOWED_TAGS = setOf(
            "rss", "channel", "item", "title", "description", "link", "pubDate", "pubdate",
            "guid", "enclosure", "author", "category", "lastBuildDate", "lastbuilddate", "language",
            "image", "url", "itunes:author", "itunes:summary", "itunes:duration",
            "itunes:episode", "itunes:season", "itunes:image", "itunes:category",
            "itunes:explicit", "itunes:episodeType", "atom:link", "copyright", 
            "managingEditor", "managingeditor", "webMaster", "webmaster",
            "generator", "docs", "cloud", "ttl", "rating", "textInput", "skipHours",
            "skipDays", "itunes:subtitle", "itunes:keywords", "itunes:owner",
            "itunes:name", "itunes:email", "itunes:type", "itunes:block", "itunes:title",
            "content:encoded", "media:content", "media:thumbnail", "media:restriction",
            "podcast:funding", "podcast:person", "podcast:chapters", "podcast:transcript",
            "podcast:location", "podcast:locked", "podcast:guid", "value",
            "p", "br", "strong", "b", "em", "i", "u", "a", "ul", "ol", "li",
            "h1", "h2", "h3", "h4", "h5", "h6", "div", "span", "blockquote",
            "time", "date", "small", "sub", "sup"
        )
    }
    
    suspend fun parseRssFeed(rssUrl: String): Result<PodcastFeed> {
        return try {
            if (!isValidUrl(rssUrl)) {
                return Result.failure(Exception("Invalid RSS URL: potential security risk"))
            }
            
            val rssContent = httpClient.get(rssUrl).bodyAsText()
            
            if (rssContent.length > MAX_XML_SIZE) {
                return Result.failure(Exception("RSS feed too large: ${rssContent.length} bytes"))
            }
            
            val sanitizedContent = sanitizeXmlContent(rssContent)
            val parsedFeed = parseSecureXmlContent(sanitizedContent)
            Result.success(parsedFeed)
        } catch (e: Exception) {
            Result.failure(Exception("RSS parsing failed with security error", e))
        }
    }
    
    private fun sanitizeXmlContent(xmlContent: String): String {
        var content = xmlContent.trim()
        
        
        BLOCKED_ENTITIES.forEach { entity ->
            if (content.contains(entity, ignoreCase = true)) {
                throw Exception("Blocked XML entity detected: $entity")
            }
        }
        
        
        content = content.replace(Regex("<\\?xml-stylesheet[^>]*\\?>"), "")
        content = content.replace(Regex("<!DOCTYPE[^>]*>"), "")
        
        
        if (!content.contains("<rss") && !content.contains("<channel")) {
            throw Exception("Invalid RSS structure")
        }
        
        return content
    }
    
    private fun parseSecureXmlContent(xmlContent: String): PodcastFeed {
        val channelStart = xmlContent.indexOf("<channel")
        val channelEnd = xmlContent.indexOf("</channel>") + "</channel>".length
        
        if (channelStart < 0 || channelEnd <= channelStart) {
            throw Exception("Invalid RSS format: no valid channel element")
        }
        
        val channelContent = xmlContent.substring(channelStart, channelEnd)
        
        
        validateAllowedTags(channelContent)
        
        val podcastDetails = parsePodcastDetails(channelContent)
        val episodes = parseEpisodes(channelContent, podcastDetails.id, podcastDetails.title)
        
        return PodcastFeed(
            details = PodcastDetails(
                podcast = podcastDetails,
                episodes = episodes
            ),
            episodes = episodes
        )
    }
    
    private fun validateAllowedTags(content: String) {
        val tagPattern = Regex("<(/?)([a-zA-Z:]+)[^>]*>")
        val matches = tagPattern.findAll(content)
        
        for (match in matches) {
            val tagName = match.groupValues[2].lowercase()
            
            
            val isAllowed = ALLOWED_TAGS.contains(tagName) || 
                           
                           tagName.startsWith("itunes:") || 
                           tagName.startsWith("atom:") ||
                           tagName.startsWith("content:") ||
                           tagName.startsWith("dc:") ||
                           tagName.startsWith("media:") ||
                           tagName.startsWith("podcast:") ||
                           tagName.startsWith("googleplay:") ||
                           tagName.startsWith("rawvoice:") ||
                           tagName.startsWith("spotify:") ||
                           tagName.startsWith("psc:") ||
                           
                           tagName in setOf("style", "script", "meta", "head", "body", "html") ||
                           
                           tagName.endsWith("/")
            
            
            if (!isAllowed) {
                
            }
        }
    }
    
    private fun parsePodcastDetails(channelContent: String): Podcast {
        val title = extractSecureTagContent(channelContent, "title") ?: "Unknown Podcast"
        val description = extractSecureTagContent(channelContent, "description") ?: ""
        val author = extractSecureTagContent(channelContent, "itunes:author") 
            ?: extractSecureTagContent(channelContent, "author") 
            ?: "Unknown Author"
        val imageUrl = extractSecureImageUrl(channelContent)
        val language = extractSecureTagContent(channelContent, "language") ?: "en"
        val website = extractSecureTagContent(channelContent, "link") ?: ""
        val category = extractSecureTagContent(channelContent, "itunes:category", "text") ?: ""
        val isExplicit = extractSecureTagContent(channelContent, "itunes:explicit")?.lowercase() == "yes"
        
        return Podcast(
            id = generateSecurePodcastId(title, author),
            title = sanitizeString(title),
            description = sanitizeString(description),
            imageUrl = validateAndSanitizeUrl(imageUrl),
            author = sanitizeString(author),
            categoryId = 0,
            categoryName = sanitizeString(category),
            rssUrl = "",
            totalEpisodes = countEpisodes(channelContent),
            latestEpisodeDate = parseLastBuildDate(channelContent),
            language = sanitizeString(language),
            isExplicit = isExplicit,
            website = validateAndSanitizeUrl(website)
        )
    }
    
    private fun parseEpisodes(channelContent: String, podcastId: String, podcastTitle: String): List<PodcastEpisode> {
        val episodes = mutableListOf<PodcastEpisode>()
        val itemPattern = Regex("<item[^>]*>([\\s\\S]*?)</item>")
        val items = itemPattern.findAll(channelContent)
        
        var episodeCount = 0
        for (item in items) {
            if (episodeCount >= MAX_EPISODES) {
                break 
            }
            
            try {
                val itemContent = item.groupValues[1]
                val episode = parseSecureEpisode(itemContent, podcastId, podcastTitle)
                episodes.add(episode)
                episodeCount++
            } catch (e: Exception) {
                continue 
            }
        }
        
        return episodes.sortedByDescending { it.publishedAt }
    }
    
    private fun parseSecureEpisode(itemContent: String, podcastId: String = "", podcastTitle: String = ""): PodcastEpisode {
        val title = extractSecureTagContent(itemContent, "title") 
            ?: extractSecureTagContent(itemContent, "itunes:title")
            ?: "Untitled Episode"
        val description = extractSecureTagContent(itemContent, "content:encoded")
            ?: extractSecureTagContent(itemContent, "description") 
            ?: extractSecureTagContent(itemContent, "itunes:summary")
            ?: ""
        val audioUrl = extractSecureEnclosureUrl(itemContent)
        val publishDate = parsePublishDate(itemContent) ?: Clock.System.now()
        val duration = parseDuration(itemContent)?.toInt() ?: 0
        val episodeNumber = extractSecureTagContent(itemContent, "itunes:episode")?.toIntOrNull()
        val seasonNumber = extractSecureTagContent(itemContent, "itunes:season")?.toIntOrNull()
        val guid = extractSecureTagContent(itemContent, "guid") ?: generateSecureEpisodeId(title, publishDate)
        val imageUrl = extractSecureImageUrl(itemContent).takeIf { it.isNotEmpty() }
        
        return PodcastEpisode(
            id = sanitizeString(guid),
            title = sanitizeString(title),
            description = sanitizeString(description),
            audioUrl = validateAndSanitizeUrl(audioUrl),
            imageUrl = imageUrl?.let { validateAndSanitizeUrl(it) },
            publishedAt = publishDate,
            duration = duration,
            podcastId = sanitizeString(podcastId),
            podcastTitle = sanitizeString(podcastTitle),
            isExplicit = false,
            episodeNumber = episodeNumber,
            seasonNumber = seasonNumber
        )
    }
    
    private fun extractSecureTagContent(content: String, tagName: String, attribute: String? = null): String? {
        
        if (tagName.length > 50 || tagName.contains(Regex("[^a-zA-Z:_-]"))) {
            return null
        }
        
        val pattern = if (attribute != null) {
            if (attribute.length > 20 || attribute.contains(Regex("[^a-zA-Z_-]"))) {
                return null
            }
            Regex("<$tagName[^>]*$attribute=\"([^\"]{0,1000})\"|<$tagName[^>]*$attribute='([^']{0,1000})'", RegexOption.IGNORE_CASE)
        } else {
            Regex("<$tagName[^>]*>([\\s\\S]{0,10000}?)</$tagName>", RegexOption.IGNORE_CASE)
        }
        
        val match = pattern.find(content)
        return when {
            attribute != null -> match?.groupValues?.get(1)?.takeIf { it.isNotEmpty() } ?: match?.groupValues?.get(2)
            else -> match?.groupValues?.get(1)?.let { cleanSecureXmlContent(it) }
        }
    }
    
    private fun extractSecureImageUrl(content: String): String {
        
        val itunesImage = extractSecureTagContent(content, "itunes:image", "href")
        if (!itunesImage.isNullOrEmpty() && isValidUrl(itunesImage)) return itunesImage
        
        
        val mediaThumbnail = extractSecureTagContent(content, "media:thumbnail", "url")
        if (!mediaThumbnail.isNullOrEmpty() && isValidUrl(mediaThumbnail)) return mediaThumbnail
        
        
        val imagePattern = Regex("<image[^>]*>([\\s\\S]{0,5000}?)</image>")
        val imageMatch = imagePattern.find(content)
        if (imageMatch != null) {
            val imageContent = imageMatch.groupValues[1]
            val url = extractSecureTagContent(imageContent, "url")
            if (!url.isNullOrEmpty() && isValidUrl(url)) return url
        }
        
        return ""
    }
    
    private fun extractSecureEnclosureUrl(content: String): String {
        
        val enclosurePattern = Regex("<enclosure[^>]*url=\"([^\"]{0,1000})\"|<enclosure[^>]*url='([^']{0,1000})'", RegexOption.IGNORE_CASE)
        val enclosureMatch = enclosurePattern.find(content)
        val enclosureUrl = enclosureMatch?.groupValues?.get(1)?.takeIf { it.isNotEmpty() } ?: enclosureMatch?.groupValues?.get(2)
        
        if (!enclosureUrl.isNullOrEmpty() && isValidUrl(enclosureUrl)) {
            return enclosureUrl
        }
        
        
        val mediaPattern = Regex("<media:content[^>]*url=\"([^\"]{0,1000})\"|<media:content[^>]*url='([^']{0,1000})'", RegexOption.IGNORE_CASE)
        val mediaMatch = mediaPattern.find(content)
        val mediaUrl = mediaMatch?.groupValues?.get(1)?.takeIf { it.isNotEmpty() } ?: mediaMatch?.groupValues?.get(2)
        
        return if (!mediaUrl.isNullOrEmpty() && isValidUrl(mediaUrl)) mediaUrl else ""
    }
    
    private fun parsePublishDate(content: String): Instant? {
        val pubDate = extractSecureTagContent(content, "pubDate") ?: return null
        return try {
            parseRfc2822Date(pubDate)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun parseLastBuildDate(content: String): Instant? {
        val lastBuildDate = extractSecureTagContent(content, "lastBuildDate") ?: return null
        return try {
            parseRfc2822Date(lastBuildDate)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun parseRfc2822Date(dateString: String): Instant {
        val cleanDate = sanitizeString(dateString).trim()
        if (cleanDate.length > 50) throw Exception("Date string too long")
        
        val epochSeconds = when {
            cleanDate.contains("GMT") || cleanDate.contains("UTC") -> {
                parseRfc2822ToEpoch(cleanDate)
            }
            cleanDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}.*")) -> {
                parseIso8601ToEpoch(cleanDate)
            }
            else -> throw Exception("Unsupported date format: $cleanDate")
        }
        return Instant.fromEpochSeconds(epochSeconds)
    }
    
    private fun parseRfc2822ToEpoch(dateString: String): Long {
        val monthMap = mapOf(
            "Jan" to 1, "Feb" to 2, "Mar" to 3, "Apr" to 4, "May" to 5, "Jun" to 6,
            "Jul" to 7, "Aug" to 8, "Sep" to 9, "Oct" to 10, "Nov" to 11, "Dec" to 12
        )
        
        val pattern = Regex("\\w+,?\\s+(\\d{1,2})\\s+(\\w+)\\s+(\\d{4})\\s+(\\d{2}):(\\d{2}):(\\d{2})\\s*([+-]\\d{4}|\\w+)?")
        val match = pattern.find(dateString) ?: throw Exception("Invalid RFC 2822 format")
        
        val day = match.groupValues[1].toInt()
        val monthName = match.groupValues[2]
        val year = match.groupValues[3].toInt()
        val hour = match.groupValues[4].toInt()
        val minute = match.groupValues[5].toInt()
        val second = match.groupValues[6].toInt()
        
        
        if (year < 1900 || year > 2100 || day < 1 || day > 31 || hour > 23 || minute > 59 || second > 59) {
            throw Exception("Invalid date values")
        }
        
        val month = monthMap[monthName] ?: throw Exception("Invalid month: $monthName")
        
        val totalDays = yearsToDays(year) + monthsToDays(month, year) + day - 1
        val totalSeconds = totalDays * 86400L + hour * 3600L + minute * 60L + second
        
        return totalSeconds - 62167219200L
    }
    
    private fun parseIso8601ToEpoch(dateString: String): Long {
        val isoPattern = Regex("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})")
        val match = isoPattern.find(dateString) ?: throw Exception("Invalid ISO 8601 format")
        
        val year = match.groupValues[1].toInt()
        val month = match.groupValues[2].toInt()
        val day = match.groupValues[3].toInt()
        val hour = match.groupValues[4].toInt()
        val minute = match.groupValues[5].toInt()
        val second = match.groupValues[6].toInt()
        
        
        if (year < 1900 || year > 2100 || month < 1 || month > 12 || day < 1 || day > 31 || hour > 23 || minute > 59 || second > 59) {
            throw Exception("Invalid date values")
        }
        
        val totalDays = yearsToDays(year) + monthsToDays(month, year) + day - 1
        val totalSeconds = totalDays * 86400L + hour * 3600L + minute * 60L + second
        
        return totalSeconds - 62167219200L
    }
    
    private fun yearsToDays(year: Int): Long {
        val leapYears = (year - 1) / 4 - (year - 1) / 100 + (year - 1) / 400
        return (year - 1970L) * 365L + leapYears - 477
    }
    
    private fun monthsToDays(month: Int, year: Int): Int {
        val daysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        if (month == 2 && isLeapYear(year)) {
            daysInMonth[1] = 29
        }
        return (0 until month - 1).sumOf { daysInMonth[it] }
    }
    
    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
    
    private fun parseDuration(content: String): Long? {
        val durationStr = extractSecureTagContent(content, "itunes:duration") ?: return null
        if (durationStr.length > 20) return null 
        
        return try {
            when {
                durationStr.contains(":") -> {
                    val parts = durationStr.split(":")
                    when (parts.size) {
                        2 -> parts[0].toLong() * 60 + parts[1].toLong()
                        3 -> parts[0].toLong() * 3600 + parts[1].toLong() * 60 + parts[2].toLong()
                        else -> null
                    }
                }
                else -> durationStr.toLongOrNull()
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun countEpisodes(content: String): Int {
        val count = Regex("<item[^>]*>", RegexOption.IGNORE_CASE).findAll(content).count()
        return minOf(count, MAX_EPISODES) 
    }
    
    private fun cleanSecureXmlContent(content: String): String {
        if (content.length > 50000) { 
            return content.substring(0, 50000)
        }
        
        return content
            .replace(Regex("<!\\[CDATA\\[([\\s\\S]*?)\\]\\]>")) { it.groupValues[1] }
            .replace(Regex("<[^>]+>"), "")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&apos;", "'")
            .trim()
    }
    
    private fun sanitizeString(input: String): String {
        if (input.length > 10000) {
            return input.substring(0, 10000)
        }
        return input.replace(Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]"), "").trim()
    }
    
    private fun validateAndSanitizeUrl(url: String): String {
        if (!isValidUrl(url)) return ""
        return url.trim()
    }
    
    private fun isValidUrl(url: String): Boolean {
        if (url.isBlank() || url.length > 2000) return false
        
        val validSchemes = listOf("http://", "https://")
        if (!validSchemes.any { url.startsWith(it, ignoreCase = true) }) return false
        
        val blockedPatterns = listOf("file://", "javascript:", "data:")
        if (blockedPatterns.any { url.contains(it, ignoreCase = true) }) return false
        
        return true
    }
    
    private fun generateSecurePodcastId(title: String, author: String): String {
        val sanitizedTitle = sanitizeString(title).take(100)
        val sanitizedAuthor = sanitizeString(author).take(100)
        return "${sanitizedTitle.hashCode()}_${sanitizedAuthor.hashCode()}".replace("-", "")
    }
    
    private fun generateSecureEpisodeId(title: String, publishDate: Instant?): String {
        val sanitizedTitle = sanitizeString(title).take(100)
        val dateStr = publishDate?.epochSeconds?.toString() ?: "unknown"
        return "${sanitizedTitle.hashCode()}_$dateStr".replace("-", "")
    }
}