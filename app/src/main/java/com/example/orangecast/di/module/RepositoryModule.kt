package com.example.orangecast.di.module

import com.example.orangecast.data.Api
import com.example.orangecast.data.repository.FeedRepository
import com.example.orangecast.data.repository.SearchRepository
import com.example.orangecast.data.utils.XmlParser
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