package com.example.orangecast.di.module

import com.example.orangecast.BuildConfig
import com.example.orangecast.data.itunes.ITunesApi
import com.example.orangecast.data.listennotes.ListenApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideITunesApi(): ITunesApi {
        return Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideListenNotesApi(): ListenApi {
        val client = OkHttpClient.Builder()
        client.addInterceptor { chain ->
            val original = chain.request()

            val request = original.newBuilder()
                .header("X-ListenAPI-Key", BuildConfig.LISTEN_NOTES_API_KEY)
                .method(original.method, original.body)
                .build()

            return@addInterceptor chain.proceed(request)
        }
        return Retrofit.Builder()
            .baseUrl("https://listen-api.listennotes.com/api/v2/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client.build())
            .build()
            .create(ListenApi::class.java)
    }

}