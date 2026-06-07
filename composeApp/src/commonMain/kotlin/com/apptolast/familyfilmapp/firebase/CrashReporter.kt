package com.apptolast.familyfilmapp.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics

open class CrashReporter {
    open fun log(message: String) {
        Firebase.crashlytics.log(message)
    }

    open fun recordException(throwable: Throwable) {
        Firebase.crashlytics.recordException(throwable)
    }

    open fun setUserId(userId: String?) {
        Firebase.crashlytics.setUserId(userId.orEmpty())
    }

    open fun setCustomKey(key: String, value: String) {
        Firebase.crashlytics.setCustomKey(key, value)
    }

    open fun setEnabled(enabled: Boolean) {
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(enabled)
    }
}
