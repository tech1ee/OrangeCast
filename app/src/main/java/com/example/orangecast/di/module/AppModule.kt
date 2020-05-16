package com.example.orangecast.di.module

import android.content.Context
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

    @Singleton
    @Provides
    fun provideApplicationContext(app: App): Context {
        return app.applicationContext
    }
}