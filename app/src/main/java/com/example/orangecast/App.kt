package com.example.orangecast

import android.app.Application
import android.content.Context
import com.example.orangecast.di.*
import com.example.orangecast.data.api.SearchApi
import com.example.orangecast.di.module.ApiModule
import com.example.orangecast.di.module.AppModule
import com.facebook.stetho.Stetho

class App : Application() {

    private var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        initDagger()
    }

    private fun initDagger() {
        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .apiModule(ApiModule())
            .build()

        appComponent?.inject(this)
    }

    companion object {
        var searchApi: SearchApi? = null

        @JvmStatic
        fun appComponent(context: Context?): AppComponent? {
            return (context?.applicationContext as? App)?.appComponent
        }
    }
}