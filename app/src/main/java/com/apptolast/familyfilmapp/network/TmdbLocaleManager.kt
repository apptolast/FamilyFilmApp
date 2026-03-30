package com.apptolast.familyfilmapp.network

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class TmdbLocaleManager @Inject constructor(private val prefs: SharedPreferences) {

    private val _languageTag = MutableStateFlow(
        prefs.getString(PREF_LANGUAGE_TAG, null)
            ?: Locale.getDefault().toLanguageTag(),
    )
    val languageTag: StateFlow<String> = _languageTag.asStateFlow()

    val countryCode: String
        get() {
            val tag = _languageTag.value
            val parts = tag.split("-")
            return if (parts.size >= 2) parts[1] else Locale.getDefault().country
        }

    fun update(languageTag: String) {
        val effective = languageTag.takeIf { it.isNotBlank() }
            ?: Locale.getDefault().toLanguageTag()
        _languageTag.value = effective
        prefs.edit { putString(PREF_LANGUAGE_TAG, effective) }
        Timber.d("TmdbLocaleManager updated: languageTag=$effective, countryCode=$countryCode")
    }

    companion object {
        const val PREFS_NAME = "tmdb_locale_prefs"
        private const val PREF_LANGUAGE_TAG = "tmdb_language_tag"
    }
}
