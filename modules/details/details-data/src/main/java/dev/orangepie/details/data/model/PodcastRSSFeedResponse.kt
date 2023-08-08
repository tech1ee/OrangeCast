package dev.orangepie.details.data.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
data class PodcastRSSFeedResponse(
    @field:Element(name = "channel")
    val channel: PodcastRSSFeedChannelResponse,
)

@Root(name = "channel", strict = false)
data class PodcastRSSFeedChannelResponse(
    @field:Element(name = "description")
    val description: String,
    @field:ElementList(name = "item", inline = true, required = false)
    val items: List<PodcastRSSFeedItemResponse>,
)

@Root(name = "item", strict = false)
data class PodcastRSSFeedItemResponse(
    @field:Element(name = "title")
    val title: String,
    @field:Element(name = "description")
    val description: String,
    @field:Element(name = "link")
    val link: String,
    @field:Element(name = "pubDate")
    val pubDate: String,
    @field:Element(name = "enclosure")
    val enclosure: PodcastRSSFeedEnclosureResponse,
    @field:Element(name = "itunes:duration")
    val itunesDuration: String,
    @field:Element(name = "itunes:summary")
    val itunesSummary: String?,
)

@Root(name = "enclosure", strict = false)
data class PodcastRSSFeedEnclosureResponse(
    @field:Element(name = "url")
    val url: String,
    @field:Element(name = "length")
    val length: String,
    @field:Element(name = "type")
    val type: String,
)
