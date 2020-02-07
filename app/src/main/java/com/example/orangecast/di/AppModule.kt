package com.example.orangecast.di

import com.example.orangecast.App
import com.example.orangecast.interactor.DiscoverInteractor
import com.example.orangecast.network.Api
import com.example.orangecast.network.repository.Repository
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
    fun provideDiscoverInteractor(repository: Repository): DiscoverInteractor {
        return DiscoverInteractor(repository)
    }

    @Singleton
    @Provides
    fun provideDiscoverViewModel(interactor: DiscoverInteractor): DiscoverViewModel {
        return DiscoverViewModel(interactor)
    }
}