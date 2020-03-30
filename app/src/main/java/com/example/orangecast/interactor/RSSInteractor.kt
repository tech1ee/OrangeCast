package com.example.orangecast.interactor

import com.example.orangecast.data.Episode
import com.prof.rssparser.Article
import com.prof.rssparser.Parser
import io.reactivex.Single
import java.util.ArrayList

class RSSInteractor: BaseInteractor() {

    private val parser = Parser()

    fun getEpisodesFromRSS(url: String): Single<List<Episode>> {
        return Single.create { emitter ->
            parser.execute(url)
            parser.onFinish(object : Parser.OnTaskCompleted {
                override fun onError() {
                    emitter.onError(Throwable())
                }

                override fun onTaskCompleted(feed: ArrayList<Article>?) {
                    val list = arrayListOf<Episode>()
                    feed?.forEach {
                        list.add(
                            Episode(it.title,it.author, it.link,it.pubDate, it.description
                                , it.content, it.image, it.categories)
                        )
                    }
                    emitter.onSuccess(list)
                }

            })
        }
    }
}