package com.orangecast

import android.app.Application
import dev.orangecast.shared.di.sharedModule
import dev.orangecast.shared.sync.EpisodeSyncWorker
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Napier logging for Android
        Napier.base(DebugAntilog())
        
        startKoin {
            androidContext(this@App)
            modules(sharedModule)
        }
        
        // Start periodic episode sync
        EpisodeSyncWorker.startPeriodicSync(this)
    }
}