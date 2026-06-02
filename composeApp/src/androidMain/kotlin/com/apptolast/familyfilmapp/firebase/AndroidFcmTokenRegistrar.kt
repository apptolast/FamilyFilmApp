package com.apptolast.familyfilmapp.firebase

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics

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
    }
}
