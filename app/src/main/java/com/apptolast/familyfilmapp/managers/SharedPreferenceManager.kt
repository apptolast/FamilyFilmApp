package com.apptolast.familyfilmapp.managers

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferenceManager @Inject constructor(
    private val prefs: SharedPreferences,
) {

    // -----------------------
    // TOKEN
    // -----------------------
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun setToken(value: String?) {
        prefs.edit().putString(KEY_TOKEN, value).apply()
    }

    companion object {
        const val SHARED_PREFERENCES_FILE_NAME = "shared_preferences_file_name"

        private const val KEY_TOKEN = "key_token"
    }
}
