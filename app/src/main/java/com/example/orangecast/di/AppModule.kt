package com.example.orangecast.di

import com.example.orangecast.App
import com.example.orangecast.network.Api
import com.example.orangecast.network.repository.Repository
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
}