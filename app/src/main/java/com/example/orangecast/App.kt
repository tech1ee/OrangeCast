package com.example.orangecast

import android.app.Application
import com.example.orangecast.di.appModules
import com.facebook.stetho.Stetho
import org.koin.android.ext.android.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, appModules)
        Stetho.initializeWithDefaults(this)
    }
}