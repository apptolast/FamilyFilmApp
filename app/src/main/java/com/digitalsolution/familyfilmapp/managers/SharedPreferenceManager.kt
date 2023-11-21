package com.digitalsolution.familyfilmapp.managers

import android.content.SharedPreferences
import com.digitalsolution.familyfilmapp.extensions.decodeJWT
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
        setUserid(
            Integer.parseInt(
                value?.decodeJWT()?.get("id").toString(),
            ),
        )
    }

    // -----------------------
    // USER ID
    // -----------------------
    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    private fun setUserid(value: Int) = prefs.edit().putInt(KEY_USER_ID, value).apply()

    companion object {
        const val SHARED_PREFERENCES_FILE_NAME = "shared_preferences_file_name"

        private const val KEY_TOKEN = "key_token"
        private const val KEY_USER_ID = "key_user_id"
    }
}
