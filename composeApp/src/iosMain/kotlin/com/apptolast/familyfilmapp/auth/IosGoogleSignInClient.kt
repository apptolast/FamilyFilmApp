package com.apptolast.familyfilmapp.auth

import com.apptolast.familyfilmapp.firebase.CrashReporter

/**
 * iOS scaffold for [GoogleSignInClient]. The real implementation requires
 * the GoogleSignIn-iOS SPM module bound via Kotlin/Native cinterop, which
 * Xcode adds at integration time (see README → "Swift Package Manager
 * (iOS only)").
 *
 * Wiring once the SPM module is added:
 *
 * 1. In Xcode: File → Add Package Dependencies →
 *    `https://github.com/google/GoogleSignIn-iOS.git`, select the
 *    `GoogleSignIn` product on the `iosApp` target.
 *
 * 2. Add a cinterop `.def` file to expose the framework to Kotlin/Native:
 *    `composeApp/src/nativeInterop/cinterop/GoogleSignIn.def` with:
 *      language = Objective-C
 *      modules = GoogleSignIn
 *      package = cocoapods.GoogleSignIn
 *      linkerOpts = -framework GoogleSignIn
 *    and reference it in composeApp/build.gradle.kts under the
 *    `iosArm64 { compilations.getByName("main").cinterops.create("GoogleSignIn") { ... } }` blocks
 *    pointing at the .def.
 *
 * 3. In iosApp/iosApp/iOSApp.swift add the URL scheme handler:
 *    `func application(_ app: UIApplication, open url: URL, ...) -> Bool {
 *       return GIDSignIn.sharedInstance.handle(url)
 *    }`
 *    plus the `GIDClientID` key in Info.plist (Web Client ID from
 *    `BuildConfig.WEB_ID_CLIENT`, same value as Android).
 *
 * 4. Replace this class with one that calls
 *    `GIDSignIn.sharedInstance.signIn(...)` using the resulting cinterop
 *    bindings and returns the `idToken` of the resulting
 *    `GIDGoogleUser.idToken`.
 *
 * Until then signIn() reports "cancelled" (`null`) so the Koin graph
 * stays satisfied and Google Sign-In is simply a no-op on iOS — email/
 * password login still works through GitLive.
 */
class IosGoogleSignInClient(
    private val crashReporter: CrashReporter,
) : GoogleSignInClient {

    override suspend fun signIn(): String? {
        crashReporter.log("IosGoogleSignInClient.signIn() called before SPM cinterop is wired")
        return null
    }

    override suspend fun signOut() {
        // No-op until GoogleSignIn SPM cinterop is wired.
    }
}
