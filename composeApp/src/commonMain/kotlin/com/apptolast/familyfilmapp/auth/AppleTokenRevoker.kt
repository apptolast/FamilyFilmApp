package com.apptolast.familyfilmapp.auth

import com.apptolast.familyfilmapp.firebase.CrashReporter
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.functions.functions
import kotlinx.serialization.Serializable

// Calls the revokeAppleToken Cloud Function so Apple severs the app↔Apple ID link
// when the user deletes their account (App Store guideline 5.1.1(v)).
interface AppleTokenRevoker {
    suspend fun revoke(authorizationCode: String): Boolean
}

class FirebaseAppleTokenRevoker(private val crashReporter: CrashReporter) : AppleTokenRevoker {

    // Region must match the one declared in functions/src/revokeAppleToken.ts.
    private val functions get() = Firebase.functions(region = CLOUD_FUNCTION_REGION)

    override suspend fun revoke(authorizationCode: String): Boolean = runCatching {
        functions
            .httpsCallable(FUNCTION_NAME)
            .invoke(
                RevokeAppleTokenRequest.serializer(),
                RevokeAppleTokenRequest(authorizationCode = authorizationCode),
            )
            .data(RevokeAppleTokenResponse.serializer())
            .success
    }.getOrElse { error ->
        crashReporter.recordException(error)
        false
    }

    @Serializable
    private data class RevokeAppleTokenRequest(val authorizationCode: String)

    @Serializable
    private data class RevokeAppleTokenResponse(val success: Boolean = false)

    companion object {
        private const val FUNCTION_NAME = "revokeAppleToken"
        private const val CLOUD_FUNCTION_REGION = "europe-west2"
    }
}
