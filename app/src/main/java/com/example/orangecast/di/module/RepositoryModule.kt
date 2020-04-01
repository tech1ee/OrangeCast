package com.example.orangecast.di.module

import com.example.orangecast.data.api.FeedApi
import com.example.orangecast.data.api.SearchApi
import com.example.orangecast.data.repository.FeedRepository
import com.example.orangecast.data.repository.SearchRepository
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideSearchRepository(@Named(ApiModule.ITUNES) searchApi: SearchApi): SearchRepository {
        return SearchRepository(searchApi)
    }

    @Singleton
    @Provides
    fun provideFeedRepository(@Named(ApiModule.FEED) feedApi: FeedApi): FeedRepository {
        return FeedRepository(feedApi)
    }
}