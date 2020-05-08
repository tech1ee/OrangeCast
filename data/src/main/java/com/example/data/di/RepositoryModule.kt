package com.example.data.di

import com.example.data.network.Api
import com.example.data.repository.FeedRepository
import com.example.data.repository.SearchRepository
import com.example.data.utils.XmlParser
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideSearchRepository(searchApi: Api): SearchRepository {
        return SearchRepository(searchApi)
    }

    @Singleton
    @Provides
    fun provideFeedRepository(xmlParser: XmlParser): FeedRepository {
        return FeedRepository(xmlParser)
    }
}