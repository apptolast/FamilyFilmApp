package com.apptolast.familyfilmapp.auth

import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.firebase.toDomainUserModel
import com.apptolast.familyfilmapp.model.local.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.OAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.concurrent.Volatile
import kotlin.coroutines.resume

class IosAppleSignInClient(private val crashReporter: CrashReporter) : AppleSignInClient {

    override suspend fun signIn(): User? {
        val bridge = installedBridge ?: run {
            crashReporter.log("AppleSignIn bridge not installed from Swift")
            return null
        }

        val native = suspendCancellableCoroutine<NativeAppleResult?> { cont ->
            bridge.startSignIn { idToken, rawNonce, fullName, _, error ->
                if (error != null) crashReporter.log("AppleSignIn iOS error: $error")
                val result = if (idToken != null && rawNonce != null) {
                    NativeAppleResult(idToken, rawNonce, fullName)
                } else {
                    null
                }
                cont.resume(result)
            }
        } ?: return null

        return runCatching {
            val credential = OAuthProvider.credential(
                providerId = "apple.com",
                idToken = native.idToken,
                rawNonce = native.rawNonce,
            )
            val result = Firebase.auth.signInWithCredential(credential)
            result.user?.toDomainUserModel()
        }.getOrElse { e ->
            crashReporter.recordException(e)
            null
        }
    }

    override suspend fun signOut() {
        // Apple Sign-In has no per-provider sign-out; Firebase signOut happens elsewhere.
    }

    override suspend fun reauthenticateForRevocation(): String? {
        val bridge = installedBridge ?: run {
            crashReporter.log("AppleSignIn bridge not installed from Swift")
            return null
        }
        return suspendCancellableCoroutine { cont ->
            bridge.startSignIn { _, _, _, authorizationCode, error ->
                if (error != null) crashReporter.log("AppleReauth iOS error: $error")
                cont.resume(authorizationCode)
            }
        }
    }

    private data class NativeAppleResult(val idToken: String, val rawNonce: String, val fullName: String?)

    companion object {
        @Volatile
        private var installedBridge: IosAppleSignInBridge? = null

        fun installBridge(bridge: IosAppleSignInBridge) {
            installedBridge = bridge
        }
    }
}
