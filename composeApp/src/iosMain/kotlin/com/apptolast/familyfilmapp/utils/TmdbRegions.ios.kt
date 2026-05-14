package com.apptolast.familyfilmapp.utils

import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleCountryCode
import platform.Foundation.currentLocale

actual fun getCountryDisplayName(countryCode: String): String =
    NSLocale.currentLocale.displayNameForKey(NSLocaleCountryCode, countryCode) ?: countryCode
