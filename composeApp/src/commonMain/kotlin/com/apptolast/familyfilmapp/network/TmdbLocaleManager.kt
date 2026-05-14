package com.apptolast.familyfilmapp.network

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Multiplatform replacement for the legacy SharedPreferences-backed
 * TmdbLocaleManager. Persists the user-selected language tag and the
 * "include adult" flag through Russhwolf multiplatform-settings (which
 * uses SharedPreferences on Android and NSUserDefaults on iOS).
 */
class TmdbLocaleManager(private val settings: Settings) {

    private val _languageTag = MutableStateFlow(
        settings.getStringOrNull(PREF_LANGUAGE_TAG)?.takeIf { it.isNotBlank() }
            ?: systemLanguageTag(),
    )
    val languageTag: StateFlow<String> = _languageTag.asStateFlow()

    private val _includeAdult = MutableStateFlow(
        settings.getBoolean(PREF_INCLUDE_ADULT, defaultValue = false),
    )
    val includeAdult: StateFlow<Boolean> = _includeAdult.asStateFlow()

    val countryCode: String
        get() {
            val tag = _languageTag.value
            val parts = tag.split("-")
            return if (parts.size >= 2) parts[1] else systemCountryCode()
        }

    fun update(languageTag: String) {
        val effective = languageTag.takeIf { it.isNotBlank() } ?: systemLanguageTag()
        _languageTag.value = effective
        settings.putString(PREF_LANGUAGE_TAG, effective)
    }

    fun updateIncludeAdult(value: Boolean) {
        _includeAdult.value = value
        settings.putBoolean(PREF_INCLUDE_ADULT, value)
    }

    companion object {
        private const val PREF_LANGUAGE_TAG = "tmdb_language_tag"
        private const val PREF_INCLUDE_ADULT = "include_adult"
    }
}
