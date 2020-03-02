package com.example.orangecast.di

import com.example.orangecast.App
import com.example.orangecast.interactor.GenresInteractor
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
    fun provideDiscoverInteractor(repository: Repository): GenresInteractor {
        return GenresInteractor(repository)
    }

    @Singleton
    @Provides
    fun provideDiscoverViewModel(interactor: GenresInteractor): DiscoverViewModel {
        return DiscoverViewModel(interactor)
    }
}