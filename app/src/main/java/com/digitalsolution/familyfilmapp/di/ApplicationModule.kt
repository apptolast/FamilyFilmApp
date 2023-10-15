package com.digitalsolution.familyfilmapp.di

import com.digitalsolution.familyfilmapp.utils.DefaultDispatcherProvider
import com.digitalsolution.familyfilmapp.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()

}
