package com.apptolast.familyfilmapp.auth

// Implemented in Swift; invoked from IosAppleSignInClient. Completion is called
// exactly once with (idToken, rawNonce, fullName) on success, or with
// errorMessage when something fails. fullName is only sent by Apple on the
// first authorization for a given Apple ID — subsequent sign-ins omit it.
interface IosAppleSignInBridge {
    // Distinct method name from IosGoogleSignInBridge.signIn(completion:) to avoid
    // Objective-C selector collision when both interfaces are exposed to Swift —
    // otherwise Kotlin/Native renames one to signIn(completion_:) and breaks
    // GoogleSignInBridgeImpl.swift.
    //
    // authorizationCode is needed for App Store guideline 5.1.1(v) token revocation
    // on account deletion. The regular sign-in flow ignores it.
    fun startSignIn(
        completion: (
            idToken: String?,
            rawNonce: String?,
            fullName: String?,
            authorizationCode: String?,
            errorMessage: String?,
        ) -> Unit,
    )
}
