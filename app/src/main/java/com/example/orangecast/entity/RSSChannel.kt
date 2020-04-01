package com.example.orangecast.entity

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "channel", strict = false)
data class RSSChannel(
    @ElementList(inline = true, required = false)
    val item: List<RSSItem>
)

@Root(name = "item")
data class RSSItem(
    @Element val title: String?,
    @Element val pubDate: String?,
    @Element val link: String?,
    @Element(name = "itunes:image") val image: String?,
    @Element val description: String?,
    @Element(name = "itunes:duration") val duration: String?,
    @Element(name = "itunes:episode") val episode: String?
)