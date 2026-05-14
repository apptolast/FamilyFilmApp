package com.apptolast.familyfilmapp.firebase

/**
 * iOS App Check provider factory installation is invoked from the Swift side
 * (iosApp/iosApp/iOSApp.swift) right after `FirebaseApp.configure()`. The Swift
 * code links the FirebaseAppCheck SPM module directly, so there's nothing to
 * do here — but we still need an actual to satisfy the expect declaration.
 *
 * If we ever want to drive the provider factory from Kotlin (e.g. to switch
 * Debug ↔ AppAttest based on a BuildKonfig flag), block 15 adds a cinterop
 * `.def` file binding `FirebaseAppCheck`/`FIRAppCheck` and replaces this stub
 * with the proper call. For now the Swift side decides the factory.
 */
actual fun installAppCheckProvider(debug: Boolean) {
    // no-op — see KDoc above.
}
