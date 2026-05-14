package com.apptolast.familyfilmapp.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

// Android-side platform bindings. Filled in by later blocks:
// - Block 9: AppDatabase builder using Room.databaseBuilder(context, ...).
// - Block 10: native Firebase App Check provider factory installer.
// - Block 11: ConnectivityObserver backed by ConnectivityManager.
actual val platformModule = module {
    single<HttpClientEngine> { OkHttp.create() }
    single<Settings> {
        SharedPreferencesSettings(
            androidContext().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE),
        )
    }
}

private const val SETTINGS_NAME = "ffa_settings"
