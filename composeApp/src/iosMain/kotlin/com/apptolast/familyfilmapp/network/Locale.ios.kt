package com.apptolast.familyfilmapp.network

import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleCountryCode
import platform.Foundation.NSLocaleLanguageCode
import platform.Foundation.currentLocale

actual fun systemLanguageTag(): String {
    val language = NSLocale.currentLocale.objectForKey(NSLocaleLanguageCode) as? String ?: "en"
    val country = NSLocale.currentLocale.objectForKey(NSLocaleCountryCode) as? String
    return if (country.isNullOrBlank()) language else "$language-$country"
}

actual fun systemCountryCode(): String = (NSLocale.currentLocale.objectForKey(NSLocaleCountryCode) as? String) ?: "US"
