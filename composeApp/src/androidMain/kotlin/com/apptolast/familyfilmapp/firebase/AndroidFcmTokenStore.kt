package com.apptolast.familyfilmapp.firebase

import android.content.Context

object AndroidFcmTokenStore {
    private const val PREFS_NAME = "ffa_fcm"
    private const val KEY_TOKEN = "registration_token"

    fun save(context: Context, token: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    fun get(context: Context): String? = context
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getString(KEY_TOKEN, null)
}
