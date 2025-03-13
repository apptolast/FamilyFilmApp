package com.apptolast.familyfilmapp.utils

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

@SuppressLint("LogNotTimber")
class ReleaseTree : Timber.Tree() {

    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        // Filter out logs that are not Warning or Error
        if (priority < Log.WARN) return

        // Add extra information to Crashlytics
        crashlytics.setCustomKey(KEY_PRIORITY, priority)
        crashlytics.setCustomKey(KEY_TAG, tag ?: "No tag")
        crashlytics.setCustomKey(KEY_MESSAGE, message)

        // If there's an exception, we upload it to Crashlytics
        throwable?.let {
            crashlytics.recordException(it)
        } ?: run {
            // If there's no throwable, we create a fake exception with the message
            crashlytics.recordException(Exception(message))
        }

        // Also log the message
        crashlytics.log(message)
    }

    companion object {
        private const val KEY_PRIORITY = "priority"
        private const val KEY_TAG = "tag"
        private const val KEY_MESSAGE = "message"
    }
}
