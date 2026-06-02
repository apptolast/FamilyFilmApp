package com.apptolast.familyfilmapp.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics

class FamilyFilmMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        AndroidFcmTokenStore.save(applicationContext, token)
        Firebase.crashlytics.log("FCM token refreshed")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Firebase.crashlytics.log("FCM message received")

        val notification = message.notification
        val title = notification?.title ?: message.data["title"]
        val body = notification?.body ?: message.data["body"]

        if (!title.isNullOrBlank() || !body.isNullOrBlank()) {
            AndroidNotificationPresenter.show(
                context = applicationContext,
                title = title,
                body = body,
            )
        }
    }
}
