package com.example.orangecast.di.module

import com.example.orangecast.data.itunes.ITunesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ITunesModule {

    @Provides
    @Singleton
    fun provideITunesApi(): ITunesApi {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.ITUNES_API)
                .addCallAdapterFactory(MoshiConverterFactory.create())
                .build()
                .create(ITunesApi::class.java)
    }

}