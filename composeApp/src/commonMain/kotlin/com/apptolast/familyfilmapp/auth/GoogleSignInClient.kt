package com.apptolast.familyfilmapp.auth

interface GoogleSignInClient {
    // Returns the Google ID token, or null if the user cancelled.
    suspend fun signIn(): String?
    suspend fun signOut()
}

class NoOpGoogleSignInClient : GoogleSignInClient {
    override suspend fun signIn(): String? = null
    override suspend fun signOut() = Unit
}
