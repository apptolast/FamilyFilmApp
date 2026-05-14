package com.apptolast.familyfilmapp.di

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

// iOS-side platform bindings. Filled in by later blocks:
// - Block 9: AppDatabase builder against NSHomeDirectory().
// - Block 10: native FirebaseAppCheck provider factory installer via cinterop.
// - Block 11: ConnectivityObserver backed by NWPathMonitor.
actual val platformModule = module {
    single<HttpClientEngine> { Darwin.create() }
    single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
}
