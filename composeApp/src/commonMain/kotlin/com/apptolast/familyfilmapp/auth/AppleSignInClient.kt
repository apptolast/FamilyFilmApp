package com.apptolast.familyfilmapp.auth

import com.apptolast.familyfilmapp.model.local.User

// Asymmetric with GoogleSignInClient on purpose: Firebase's Android Apple flow
// (startActivityForSignInWithProvider) is monolithic — it does the OAuth dance
// and the credential exchange in one call, so we can't split it into
// "get tokens → repository exchanges them" like Google. Each platform
// encapsulates the full flow and returns a domain User directly.
interface AppleSignInClient {
    suspend fun signIn(): User?
    suspend fun signOut()

    /**
     * Re-runs Apple's native sign-in sheet purely to obtain a fresh authorization
     * code that the backend can hand to /auth/revoke. Required by App Store
     * guideline 5.1.1(v) when deleting an Apple-linked account.
     *
     * Returns the authorization code on success, or null if the user cancels or
     * if the platform's underlying SDK doesn't expose the code (Android's Firebase
     * OAuthProvider, for example, hides it behind the federated sign-in flow).
     */
    suspend fun reauthenticateForRevocation(): String?
}

class NoOpAppleSignInClient : AppleSignInClient {
    override suspend fun signIn(): User? = null
    override suspend fun signOut() = Unit
    override suspend fun reauthenticateForRevocation(): String? = null
}
