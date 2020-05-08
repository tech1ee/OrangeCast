package com.example.orangecast.di

import com.example.data.di.ApiModule
import com.example.data.di.RepositoryModule
import com.example.data.di.UtilsModule
import com.example.orangecast.App
import com.example.orangecast.di.module.*
import com.example.orangecast.view.channeldetails.ChannelDetailsFragment
import com.example.orangecast.view.discover.DiscoverFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ApiModule::class, InteractorModule::class,
    RepositoryModule::class, ViewModelModule::class, UtilsModule::class])
interface AppComponent {

    fun inject(app: App)

    fun inject(fragment: DiscoverFragment)

    fun inject(fragment: ChannelDetailsFragment)

}