package com.apptolast.familyfilmapp.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics

/**
 * Multiplatform crash + diagnostic logging façade.
 *
 * Delegates to GitLive's Firebase Crashlytics wrapper, which proxies the
 * native SDK on each platform (Crashlytics Android SDK + the iOS pod that
 * SPM links into the app). No Kermit bridge needed.
 *
 * Callers should keep recordException for non-fatal but unexpected failures
 * (network errors, parse errors, etc.) and let real crashes propagate
 * naturally so the SDK captures stack traces with full context.
 */
class CrashReporter {
    fun log(message: String) {
        Firebase.crashlytics.log(message)
    }

    fun recordException(throwable: Throwable) {
        Firebase.crashlytics.recordException(throwable)
    }

    fun setUserId(userId: String?) {
        Firebase.crashlytics.setUserId(userId.orEmpty())
    }

    fun setCustomKey(key: String, value: String) {
        Firebase.crashlytics.setCustomKey(key, value)
    }

    fun setEnabled(enabled: Boolean) {
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(enabled)
    }
}
