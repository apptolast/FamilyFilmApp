package com.apptolast.familyfilmapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
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
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalStoreModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val encryptedFileName = "${SHARED_PREFERENCES_FILE_NAME}_encrypted"

        val encryptedPrefs = EncryptedSharedPreferences.create(
            encryptedFileName,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

        // Migrate from unencrypted to encrypted prefs (one-time)
        val oldPrefs = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        if (oldPrefs.all.isNotEmpty()) {
            Timber.d("Migrating SharedPreferences to encrypted storage")
            oldPrefs.all.forEach { (key, value) ->
                when (value) {
                    is String -> encryptedPrefs.edit().putString(key, value).apply()
                    is Boolean -> encryptedPrefs.edit().putBoolean(key, value).apply()
                    is Int -> encryptedPrefs.edit().putInt(key, value).apply()
                    is Long -> encryptedPrefs.edit().putLong(key, value).apply()
                    is Float -> encryptedPrefs.edit().putFloat(key, value).apply()
                }
            }
            oldPrefs.edit().clear().apply()
            Timber.d("Migration to encrypted SharedPreferences complete")
        }

        return encryptedPrefs
    }

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
