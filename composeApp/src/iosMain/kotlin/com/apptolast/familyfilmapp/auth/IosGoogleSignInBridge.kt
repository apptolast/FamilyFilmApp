package com.apptolast.familyfilmapp.auth

// Implemented in Swift; invoked from IosGoogleSignInClient. The completion is called
// exactly once with either (idToken, accessToken) on success or (null, null, errorMessage).
interface IosGoogleSignInBridge {
    fun signIn(completion: (idToken: String?, accessToken: String?, errorMessage: String?) -> Unit)
    fun signOut()
}
