package com.apptolast.familyfilmapp.auth

// On iOS, GitLive's GoogleAuthProvider.credential requires both tokens (non-null).
// Android's Credential Manager only returns the idToken; accessToken stays null there.
data class GoogleSignInTokens(val idToken: String, val accessToken: String?)

interface GoogleSignInClient {
    // Returns the tokens needed to build a Firebase credential, or null on cancel/error.
    suspend fun signIn(): GoogleSignInTokens?
    suspend fun signOut()
}

class NoOpGoogleSignInClient : GoogleSignInClient {
    override suspend fun signIn(): GoogleSignInTokens? = null
    override suspend fun signOut() = Unit
}
