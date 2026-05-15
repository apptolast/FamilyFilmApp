package com.apptolast.familyfilmapp.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.apptolast.familyfilmapp.BuildConfig
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.platform.CurrentActivityHolder
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

// Credential Manager renders a system bottom sheet anchored to the current Activity.
class AndroidGoogleSignInClient(
    private val context: Context,
    private val activityHolder: CurrentActivityHolder,
    private val crashReporter: CrashReporter,
) : GoogleSignInClient {

    private val credentialManager: CredentialManager = CredentialManager.create(context)

    private val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(
            GetSignInWithGoogleOption.Builder(serverClientId = BuildConfig.WEB_ID_CLIENT).build(),
        )
        .build()

    override suspend fun signIn(): GoogleSignInTokens? {
        val activity = activityHolder.current ?: error("No Activity available for Google sign-in")
        return try {
            val response = credentialManager.getCredential(context = activity, request = request)
            val credential = response.credential as? CustomCredential ?: return null
            if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) return null
            val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
            GoogleSignInTokens(idToken = idToken, accessToken = null)
        } catch (_: GetCredentialCancellationException) {
            null
        } catch (e: GetCredentialException) {
            crashReporter.recordException(e)
            null
        }
    }

    override suspend fun signOut() {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Throwable) {
            crashReporter.recordException(e)
        }
    }
}
