package com.apptolast.familyfilmapp.di

import org.koin.core.module.Module

// Platform-specific Koin bindings (Android Context, AppDatabase builder,
// ConnectivityObserver, DispatcherProvider, etc.). The actual implementations
// live in androidMain/PlatformModule.android.kt and iosMain/PlatformModule.ios.kt.
expect val platformModule: Module
