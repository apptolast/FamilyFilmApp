package com.apptolast.familyfilmapp.auth

import com.apptolast.familyfilmapp.firebase.CrashReporter
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.concurrent.Volatile
import kotlin.coroutines.resume

class IosGoogleSignInClient(private val crashReporter: CrashReporter) : GoogleSignInClient {

    override suspend fun signIn(): GoogleSignInTokens? {
        val bridge = installedBridge ?: run {
            crashReporter.log("GoogleSignIn bridge not installed from Swift")
            return null
        }
        return suspendCancellableCoroutine { cont ->
            bridge.signIn { idToken, accessToken, error ->
                if (error != null) crashReporter.log("GoogleSignIn iOS error: $error")
                val tokens = if (idToken != null) GoogleSignInTokens(idToken, accessToken) else null
                cont.resume(tokens)
            }
        }
    }

    override suspend fun signOut() {
        installedBridge?.signOut()
    }

    companion object {
        @Volatile
        private var installedBridge: IosGoogleSignInBridge? = null

        fun installBridge(bridge: IosGoogleSignInBridge) {
            installedBridge = bridge
        }
    }
}
