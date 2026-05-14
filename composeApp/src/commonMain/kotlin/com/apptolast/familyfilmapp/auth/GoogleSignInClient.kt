package com.apptolast.familyfilmapp.auth

/**
 * Multiplatform façade over the platform-native Google identity APIs.
 *
 * - Android (block 14): backed by androidx.credentials.CredentialManager +
 *   GoogleIdTokenCredential — the modern replacement for GoogleSignInClient.
 * - iOS (block 15): backed by the GoogleSignIn-iOS SPM module via cinterop.
 *
 * The interface is intentionally minimal: ViewModels only need an ID
 * token to hand off to Firebase Auth (`Firebase.auth.signInWithCredential`).
 * Anything UI-level (button presentation, Activity launching, etc.) stays
 * in the platform layer.
 */
interface GoogleSignInClient {
    /**
     * Triggers the platform sign-in flow and returns the Google ID token
     * on success or `null` if the user cancelled / no token was available.
     * Throws on unrecoverable errors (network, misconfiguration, etc.).
     */
    suspend fun signIn(): String?

    /** Clears any cached credential on the device. */
    suspend fun signOut()
}

/**
 * No-op default registered in commonMain so the Koin graph resolves before
 * the per-platform implementations land in blocks 14/15. Calls log nothing
 * and return as if the user cancelled — safe to leave running in any
 * environment.
 */
class NoOpGoogleSignInClient : GoogleSignInClient {
    override suspend fun signIn(): String? = null
    override suspend fun signOut() = Unit
}
