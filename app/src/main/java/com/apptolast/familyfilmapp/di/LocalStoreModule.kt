package com.apptolast.familyfilmapp.di

import android.content.Context
import android.content.SharedPreferences
import com.apptolast.familyfilmapp.managers.SharedPreferenceManager
import com.apptolast.familyfilmapp.managers.SharedPreferenceManager.Companion.SHARED_PREFERENCES_FILE_NAME
import com.apptolast.familyfilmapp.room.AppDatabase
import com.apptolast.familyfilmapp.room.group.GroupDao
import com.apptolast.familyfilmapp.room.user.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalStoreModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(prefs: SharedPreferences) = SharedPreferenceManager(prefs)

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.getInstance(context)

    @Provides
    fun providePlantDao(appDatabase: AppDatabase): GroupDao = appDatabase.groupDao()

    @Provides
    fun provideGardenPlantingDao(appDatabase: AppDatabase): UserDao = appDatabase.userDao()
}
