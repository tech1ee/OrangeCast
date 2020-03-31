package com.example.orangecast.di

import com.example.orangecast.App
import com.example.orangecast.interactor.GenresInteractor
import com.example.orangecast.interactor.RSSInteractor
import com.example.orangecast.data.network.Api
import com.example.orangecast.data.Repository
import com.example.orangecast.view.channeldetails.ChannelDetailsViewModel
import com.example.orangecast.view.discover.DiscoverViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val app: App) {

    @Singleton
    @Provides
    fun provideApplication(): App {
        return app
    }

    @Singleton
    @Provides
    fun provideRepository(api: Api): Repository {
        return Repository(api)
    }

    @Singleton
    @Provides
    fun provideDiscoverInteractor(repository: Repository): GenresInteractor {
        return GenresInteractor(repository)
    }

    @Singleton
    @Provides
    fun providesRSSInteractor(repository: Repository): RSSInteractor {
        return RSSInteractor(repository)
    }

    @Singleton
    @Provides
    fun provideDiscoverViewModel(interactor: GenresInteractor): DiscoverViewModel {
        return DiscoverViewModel(interactor)
    }

    @Singleton
    @Provides
    fun provideChannelDetailsViewModel(rssInteractor: RSSInteractor): ChannelDetailsViewModel {
        return ChannelDetailsViewModel(rssInteractor)
    }
}