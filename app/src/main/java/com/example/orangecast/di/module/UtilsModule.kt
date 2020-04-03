package com.example.orangecast.di.module

import com.example.orangecast.data.repository.XmlParser
import dagger.Module
import dagger.Provides

@Module
class UtilsModule {

    @Provides
    fun provideXmlParser(): XmlParser {
        return XmlParser()
    }
}