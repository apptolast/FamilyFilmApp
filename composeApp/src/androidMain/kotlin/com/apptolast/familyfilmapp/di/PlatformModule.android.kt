package com.apptolast.familyfilmapp.di

import android.content.Context
import androidx.room.RoomDatabase
import com.apptolast.familyfilmapp.room.AppDatabase
import com.apptolast.familyfilmapp.room.getDatabaseBuilder
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

// Android-side platform bindings. Block 10 will add the native Firebase
// App Check provider factory installer; block 11 the ConnectivityObserver.
actual val platformModule = module {
    single<HttpClientEngine> { OkHttp.create() }
    single<Settings> {
        SharedPreferencesSettings(
            androidContext().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE),
        )
    }
    single<RoomDatabase.Builder<AppDatabase>> { getDatabaseBuilder(androidContext()) }
}

private const val SETTINGS_NAME = "ffa_settings"
