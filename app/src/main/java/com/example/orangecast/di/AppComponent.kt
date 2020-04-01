package com.example.orangecast.di

import com.example.orangecast.App
import com.example.orangecast.di.module.*
import com.example.orangecast.view.channeldetails.ChannelDetailsFragment
import com.example.orangecast.view.discover.DiscoverFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ApiModule::class, InteractorModule::class,
    RepositoryModule::class, ViewModelModule::class])
interface AppComponent {

    fun inject(app: App)

    fun inject(fragment: DiscoverFragment)

    fun inject(fragment: ChannelDetailsFragment)

}