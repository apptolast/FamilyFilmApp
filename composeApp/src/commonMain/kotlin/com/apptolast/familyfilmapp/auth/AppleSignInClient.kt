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
}

class NoOpAppleSignInClient : AppleSignInClient {
    override suspend fun signIn(): User? = null
    override suspend fun signOut() = Unit
}
