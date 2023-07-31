package dev.orangepie.data.interceptor

import com.orangecast.base.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ListenNotesAuthInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.addHeader("X-ListenAPI-Key", BuildConfig.LISTEN_NOTES_API_KEY)
        return chain.proceed(builder.build())
    }
}