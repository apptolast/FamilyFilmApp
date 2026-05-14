package com.apptolast.familyfilmapp.firebase

/**
 * Installs the Firebase App Check provider factory for the current platform.
 *
 * GitLive does not expose `firebase-app-check`, so each side touches the
 * native SDK directly:
 * - Android: `FirebaseAppCheck.installAppCheckProviderFactory(...)` with
 *   PlayIntegrity in release and DebugAppCheckProviderFactory in debug.
 * - iOS: `FIRAppCheck.setAppCheckProviderFactory(...)` via cinterop bindings
 *   to the FirebaseAppCheck SPM module (block 15 wires the cinterop).
 *
 * Called once from FamilyFilmApp.onCreate on Android and from iOSApp.swift
 * on iOS, right after Firebase has been initialised.
 */
expect fun installAppCheckProvider(debug: Boolean)
