package com.orangecast

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.orangecast.shared.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@HiltAndroidApp
class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@App)
            modules(sharedModule)
        }
    }
}