package dev.orangecast.shared.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PodcastIndexResponse(
    @SerialName("status") val status: String,
    @SerialName("feeds") val feeds: List<PodcastIndexPodcast> = emptyList(),
    @SerialName("count") val count: Int = 0,
    @SerialName("description") val description: String = ""
)

@Serializable
data class PodcastIndexPodcast(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("url") val url: String, // This is the RSS feed URL!
    @SerialName("originalUrl") val originalUrl: String? = null,
    @SerialName("link") val link: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("author") val author: String? = null,
    @SerialName("ownerName") val ownerName: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("artwork") val artwork: String? = null,
    @SerialName("lastUpdateTime") val lastUpdateTime: Long? = null,
    @SerialName("lastCrawlTime") val lastCrawlTime: Long? = null,
    @SerialName("lastParseTime") val lastParseTime: Long? = null,
    @SerialName("lastGoodHttpStatusTime") val lastGoodHttpStatusTime: Long? = null,
    @SerialName("lastHttpStatus") val lastHttpStatus: Int? = null,
    @SerialName("contentType") val contentType: String? = null,
    @SerialName("itunesId") val itunesId: Long? = null,
    @SerialName("itunesType") val itunesType: String? = null,
    @SerialName("generator") val generator: String? = null,
    @SerialName("language") val language: String? = null,
    @SerialName("type") val type: Int? = null,
    @SerialName("dead") val dead: Int? = null,
    @SerialName("crawlErrors") val crawlErrors: Int? = null,
    @SerialName("parseErrors") val parseErrors: Int? = null,
    @SerialName("categories") val categories: Map<String, String>? = null,
    @SerialName("locked") val locked: Int? = null,
    @SerialName("explicit") val explicit: Boolean? = null,
    @SerialName("podcastGuid") val podcastGuid: String? = null,
    @SerialName("medium") val medium: String? = null,
    @SerialName("episodeCount") val episodeCount: Int? = null,
    @SerialName("imageUrlHash") val imageUrlHash: Long? = null,
    @SerialName("newestItemPublishTime") val newestItemPublishTime: Long? = null
)