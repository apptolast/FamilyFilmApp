package com.apptolast.familyfilmapp.di

import org.koin.dsl.module

// iOS-side platform bindings. Filled in by later blocks:
// - Block 9: AppDatabase builder against NSHomeDirectory().
// - Block 10: native FirebaseAppCheck provider factory installer via cinterop.
// - Block 11: ConnectivityObserver backed by NWPathMonitor.
actual val platformModule = module {
}
