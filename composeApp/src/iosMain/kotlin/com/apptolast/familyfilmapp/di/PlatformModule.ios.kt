package com.apptolast.familyfilmapp.di

import androidx.room.RoomDatabase
import com.apptolast.familyfilmapp.room.AppDatabase
import com.apptolast.familyfilmapp.room.getDatabaseBuilder
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

// iOS-side platform bindings. Block 10 will add the native FirebaseAppCheck
// provider factory installer via cinterop; block 11 the ConnectivityObserver.
actual val platformModule = module {
    single<HttpClientEngine> { Darwin.create() }
    single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
    single<RoomDatabase.Builder<AppDatabase>> { getDatabaseBuilder() }
}
