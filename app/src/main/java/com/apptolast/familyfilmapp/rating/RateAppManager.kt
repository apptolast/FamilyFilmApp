package com.apptolast.familyfilmapp.rating

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tracks whether the user has already been given the chance to rate the app.
 *
 * The Google Play In-App Review API does not expose whether the user actually
 * rated, so we mark the app as "rated" as soon as the review flow completes
 * (or falls back to the Play Store listing). This is a best-effort signal used
 * only to hide the "Rate the App" entry in Settings after the first attempt.
 */
@Singleton
class RateAppManager @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val hasRatedApp: StateFlow<Boolean>
        field: MutableStateFlow<Boolean> = MutableStateFlow(prefs.getBoolean(PREF_HAS_RATED_APP, false))

    fun markAsRated() {
        if (hasRatedApp.value) return
        hasRatedApp.value = true
        prefs.edit { putBoolean(PREF_HAS_RATED_APP, true) }
        Timber.d("RateAppManager: app marked as rated")
    }

    companion object {
        private const val PREFS_NAME = "rate_app_prefs"
        private const val PREF_HAS_RATED_APP = "has_rated_app"
    }
}
