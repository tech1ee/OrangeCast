package com.example.orangecast.di.module

import com.example.orangecast.data.itunes.ITunesApi
import com.example.orangecast.data.listennotes.ListenApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
        return Retrofit.Builder()
                .baseUrl("https://listen-api.listennotes.com/api/v2/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(ListenApi::class.java)
    }

}