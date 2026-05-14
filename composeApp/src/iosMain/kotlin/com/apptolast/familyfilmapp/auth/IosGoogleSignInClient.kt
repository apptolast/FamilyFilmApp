package com.apptolast.familyfilmapp.auth

import com.apptolast.familyfilmapp.firebase.CrashReporter

// No-op until the GoogleSignIn cinterop is enabled (see build.gradle.kts).
class IosGoogleSignInClient(
    private val crashReporter: CrashReporter,
) : GoogleSignInClient {

    override suspend fun signIn(): String? {
        crashReporter.log("IosGoogleSignInClient.signIn() called before SPM cinterop is wired")
        return null
    }

    override suspend fun signOut() = Unit
}
