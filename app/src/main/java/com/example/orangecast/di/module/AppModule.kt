package com.example.orangecast.di.module

import com.example.orangecast.App
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
}