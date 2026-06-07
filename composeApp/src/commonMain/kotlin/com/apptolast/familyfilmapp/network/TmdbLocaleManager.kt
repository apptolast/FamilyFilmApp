package com.apptolast.familyfilmapp.network

import com.apptolast.familyfilmapp.utils.countryCodeFromLanguageTag
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class TmdbLocaleManager(private val settings: Settings) {

    private val _languageTag = MutableStateFlow(
        settings.getStringOrNull(PREF_LANGUAGE_TAG)?.takeIf { it.isNotBlank() }
            ?: systemLanguageTag(),
    )
    open val languageTag: StateFlow<String> = _languageTag.asStateFlow()

    private val _includeAdult = MutableStateFlow(
        settings.getBoolean(PREF_INCLUDE_ADULT, defaultValue = false),
    )
    open val includeAdult: StateFlow<Boolean> = _includeAdult.asStateFlow()

    open val countryCode: String
        get() {
            return countryCodeFromLanguageTag(
                languageTag = _languageTag.value,
                fallbackCountryCode = systemCountryCode(),
            )
        }

    open fun update(languageTag: String) {
        val effective = languageTag.takeIf { it.isNotBlank() } ?: systemLanguageTag()
        _languageTag.value = effective
        settings.putString(PREF_LANGUAGE_TAG, effective)
    }

    open fun updateIncludeAdult(value: Boolean) {
        _includeAdult.value = value
        settings.putBoolean(PREF_INCLUDE_ADULT, value)
    }

    companion object {
        private const val PREF_LANGUAGE_TAG = "tmdb_language_tag"
        private const val PREF_INCLUDE_ADULT = "include_adult"
    }
}
