package com.example.orangecast.di

import com.example.orangecast.App
import com.example.orangecast.view.discover.DiscoverFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ApiModule::class])
interface AppComponent {

    fun inject(app: App)

    fun inject(discoverFragment: DiscoverFragment)

}