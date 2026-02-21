package com.apptolast.familyfilmapp.di

import android.content.Context
import androidx.room.Room
import com.apptolast.familyfilmapp.room.AppDatabase
import com.apptolast.familyfilmapp.room.group.GroupDao
import com.apptolast.familyfilmapp.room.groupmoviestatus.GroupMovieStatusDao
import com.apptolast.familyfilmapp.room.user.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TestLocalStoreModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

    @Provides
    fun providePlantDao(appDatabase: AppDatabase): GroupDao = appDatabase.groupDao()

    @Provides
    fun provideGardenPlantingDao(appDatabase: AppDatabase): UserDao = appDatabase.userDao()

    @Provides
    fun provideGroupMovieStatusDao(appDatabase: AppDatabase): GroupMovieStatusDao = appDatabase.groupMovieStatusDao()
}
