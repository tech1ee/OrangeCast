package dev.orangepie.details.data.rss

import android.content.Context
import com.prof.rssparser.Channel
import com.prof.rssparser.Parser
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RSSParser @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {

    private val parser = Parser.Builder()
        .context(context)
        .cacheExpirationMillis(3L * 60L * 60L * 1000L)
        .build()

    suspend fun parse(url: String): Channel {
        return parser.getChannel(url)
    }


}