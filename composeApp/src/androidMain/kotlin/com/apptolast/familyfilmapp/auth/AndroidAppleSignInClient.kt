package com.apptolast.familyfilmapp.auth

import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.firebase.toDomainUserModel
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.platform.CurrentActivityHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.android
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// GitLive's commonMain auth API does not expose signInWithProvider(activity, …),
// so we reach down to the native Firebase Android SDK via Firebase.auth.android.
class AndroidAppleSignInClient(
    private val activityHolder: CurrentActivityHolder,
    private val crashReporter: CrashReporter,
) : AppleSignInClient {

    override suspend fun signIn(): User? {
        val activity = activityHolder.current
            ?: error("No Activity available for Apple sign-in")

        val nativeAuth: FirebaseAuth = Firebase.auth.android

        val provider = OAuthProvider.newBuilder("apple.com", nativeAuth).apply {
            scopes = listOf("email", "name")
        }.build()

        return try {
            // If the user came back through Custom Tabs without our coroutine
            // being alive, Firebase parks the result in pendingAuthResult.
            val pendingTask = nativeAuth.pendingAuthResult
                ?: nativeAuth.startActivityForSignInWithProvider(activity, provider)

            suspendCancellableCoroutine { cont ->
                pendingTask
                    .addOnSuccessListener { cont.resume(Unit) }
                    .addOnFailureListener { cont.resumeWithException(it) }
            }

            // After the successful sign-in Firebase.auth.currentUser is populated;
            // GitLive's wrapper reads the same native instance.
            Firebase.auth.currentUser?.toDomainUserModel()
        } catch (e: Throwable) {
            crashReporter.recordException(e)
            null
        }
    }

    override suspend fun signOut() {
        // Custom Tabs session is torn down when the OAuth flow ends; nothing else to clear.
    }
}
