package com.example.orangecast.data.repository

import android.util.Log
import com.example.orangecast.entity.Episode
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.IOException
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

class XmlParser {

    fun parseFeed(feed: String): List<Episode> {
        val list = arrayListOf<Episode>()
        try {
            val doc = getDomElements(feed)
            val nodeList = doc?.getElementsByTagName(TAG_CHANNEL)
            val element = nodeList?.item(0) as? Element

            val items = element?.getElementsByTagName(TAG_ITEM) as? NodeList
            if (items != null) {
                for (i in 0..items.length) {
                    val itemElement = items.item(i) as? Element

                    val title = getValue(itemElement, TAG_TITLE)
                    val link = getValue(itemElement, TAG_LINK)
                    val pubDate = getValue(itemElement, TAG_PUB_DATE)
                    val description = getValue(itemElement, TAG_DESRIPTION)
                    val duration = getValue(itemElement, TAG_DURATION)
                    val image = getValue(itemElement, TAG_IMAGE)
                    val episodeNumber = getValue(itemElement, TAG_EPISODE)

                    list.add(Episode(title, link, pubDate, description, duration, image, episodeNumber))
                }
            }
        } catch (e: Exception) {
            Log.e("parseFeed", e.localizedMessage ?: "")
            e.printStackTrace()
        }
        return list
    }

    private fun getDomElements(xml: String): Document? {
        var doc: Document? = null
        val docBuilderFactory = DocumentBuilderFactory.newInstance()

        try {
            val docBuilder = docBuilderFactory.newDocumentBuilder()
            val inputSource = InputSource()
            inputSource.characterStream = StringReader(xml)
            doc = docBuilder.parse(inputSource)
        } catch (e: ParserConfigurationException) {
            Log.e("DomElements", e.localizedMessage ?: "")
            e.printStackTrace()
            return null
        } catch (e: SAXException) {
            Log.e("DomElements", e.localizedMessage ?: "")
            e.printStackTrace()
            return null
        } catch (e: IOException) {
            Log.e("DomElements", e.localizedMessage ?: "")
            return null
        }
        return doc
    }

    private fun getValue(item: Element?, string: String): String? {
        val nodeList = item?.getElementsByTagName(string)
        return getElementValue(nodeList?.item(0))
    }

    private fun getElementValue(elem: Node?): String {
        var child: Node?
        if (elem != null) {
            if (elem.hasChildNodes()) {
                child = elem.firstChild
                while (child != null) {
                    if (child.nodeType == Node.TEXT_NODE || child.nodeType == Node.CDATA_SECTION_NODE) {
                        return child.nodeValue
                    }
                    child = child.nextSibling
                }
            }
        }
        return ""
    }

    companion object {
        private const val TAG_CHANNEL = "channel"
        private const val TAG_TITLE = "title"
        private const val TAG_LINK = "link"
        private const val TAG_DESRIPTION = "description"
        private const val TAG_DURATION = "duration"
        private const val TAG_ITEM = "item"
        private const val TAG_PUB_DATE = "pubDate"
        private const val TAG_IMAGE = "image"
        private const val TAG_EPISODE = "episode"
    }
}