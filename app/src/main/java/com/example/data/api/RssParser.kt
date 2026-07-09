package com.example.data.api

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RssPost(
    val title: String = "",
    val link: String = "",
    val description: String = "",
    val pubDate: String = "",
    val imageUrl: String = ""
)

class RssParser {
    fun parse(inputStream: InputStream): List<RssPost> {
        val parser = Xml.newPullParser()
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

        val posts = mutableListOf<RssPost>()
        var eventType = parser.eventType
        var currentPost = RssPost()
        var text = ""
        var insideItem = false

        try {
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (tagName.equals("item", ignoreCase = true)) {
                            currentPost = RssPost()
                            insideItem = true
                        } else if (insideItem && (tagName.equals("media:content", ignoreCase = true) || 
                                                  tagName.equals("media:thumbnail", ignoreCase = true) || 
                                                  tagName.equals("enclosure", ignoreCase = true))) {
                            val imageUrl = parser.getAttributeValue(null, "url")
                            if (!imageUrl.isNullOrBlank()) {
                                currentPost = currentPost.copy(imageUrl = imageUrl)
                            }
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if (insideItem) {
                            text = parser.text
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (tagName.equals("item", ignoreCase = true)) {
                            posts.add(currentPost)
                            insideItem = false
                        } else if (insideItem) {
                            when {
                                tagName.equals("title", ignoreCase = true) -> {
                                    currentPost = currentPost.copy(title = text.trim())
                                }
                                tagName.equals("link", ignoreCase = true) -> {
                                    currentPost = currentPost.copy(link = text.trim())
                                }
                                tagName.equals("description", ignoreCase = true) -> {
                                    val cleanDesc = text.trim()
                                    // Extract image if not already set from media tags
                                    var updatedPost = currentPost.copy(description = stripHtml(cleanDesc))
                                    if (updatedPost.imageUrl.isEmpty()) {
                                        val extractedImg = extractImageUrl(cleanDesc)
                                        if (extractedImg != null) {
                                            updatedPost = updatedPost.copy(imageUrl = extractedImg)
                                        }
                                    }
                                    currentPost = updatedPost
                                }
                                tagName.equals("pubDate", ignoreCase = true) -> {
                                    currentPost = currentPost.copy(pubDate = text.trim())
                                }
                            }
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return posts
    }

    private fun extractImageUrl(html: String): String? {
        return try {
            val regex = """<img[^>]+src=["']([^"']+)["']""".toRegex(RegexOption.IGNORE_CASE)
            val matchResult = regex.find(html)
            matchResult?.groups?.get(1)?.value
        } catch (e: Exception) {
            null
        }
    }

    private fun stripHtml(html: String): String {
        return try {
            // Replace line breaks with newlines
            var clean = html
                .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
                .replace(Regex("</p>", RegexOption.IGNORE_CASE), "\n")
                .replace(Regex("</div>", RegexOption.IGNORE_CASE), "\n")
            
            // Remove all HTML tags
            clean = clean.replace(Regex("<[^>]*>"), "")
            
            // Unescape common HTML entities
            clean = clean
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&#39;", "'")
                .replace("&nbsp;", " ")
            
            clean.trim()
        } catch (e: Exception) {
            html
        }
    }

    companion object {
        fun parsePubDate(pubDateStr: String): Long {
            if (pubDateStr.isBlank()) return System.currentTimeMillis()
            val formats = listOf(
                SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US),
                SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US),
                SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            )
            for (format in formats) {
                try {
                    val date = format.parse(pubDateStr)
                    if (date != null) return date.time
                } catch (e: Exception) {
                    // Try next format
                }
            }
            return System.currentTimeMillis() // Fallback
        }
    }
}
