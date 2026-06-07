package com.apptolast.familyfilmapp.firebase

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import java.util.Locale

object AndroidFcmTokenRegistrar {
    fun refreshToken(context: Context) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Firebase.crashlytics.log("FCM token fetch failed")
                    task.exception?.let { Firebase.crashlytics.recordException(it) }
                    return@addOnCompleteListener
                }

                AndroidFcmTokenStore.save(context, task.result)
                Firebase.crashlytics.log("FCM token fetched")
            }

        subscribeToReleaseTopic()
    }

    // Subscribe each device to a single language-scoped "new releases" topic so the
    // n8n workflow can fan out localized push notifications. Re-runs on every launch,
    // self-healing when the device language changes (subscribe correct + drop the other).
    private fun subscribeToReleaseTopic() {
        val lang = if (Locale.getDefault().language == "es") "es" else "en"
        val other = if (lang == "es") "en" else "es"
        FirebaseMessaging.getInstance().subscribeToTopic("new_releases_$lang")
        FirebaseMessaging.getInstance().unsubscribeFromTopic("new_releases_$other")
    }
}
