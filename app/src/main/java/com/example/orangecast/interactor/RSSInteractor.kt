package com.example.orangecast.interactor

import android.util.Log
import com.prof.rssparser.Article
import com.prof.rssparser.Parser
import io.reactivex.Single
import java.util.ArrayList

class RSSInteractor: BaseInteractor() {

    private val parser = Parser()

    fun getRSSFeed(url: String): Single<List<String>> {
        return Single.create { emitter ->
            parser.execute(url)
            parser.onFinish(object : Parser.OnTaskCompleted {
                override fun onError() {
                    emitter.onError(Throwable())
                }

                override fun onTaskCompleted(feed: ArrayList<Article>?) {
                    feed?.forEach { Log.e(it.author, it.link) }
                }

            })
        }
    }
}