package com.apptolast.familyfilmapp.di

import org.koin.dsl.module

// Android-side platform bindings. Filled in by later blocks:
// - Block 9: AppDatabase builder using Room.databaseBuilder(context, ...).
// - Block 10: native Firebase App Check provider factory installer.
// - Block 11: ConnectivityObserver backed by ConnectivityManager.
actual val platformModule = module {
}
