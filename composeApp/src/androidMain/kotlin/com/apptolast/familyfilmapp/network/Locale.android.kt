package com.apptolast.familyfilmapp.network

import java.util.Locale

actual fun systemLanguageTag(): String = Locale.getDefault().toLanguageTag()

actual fun systemCountryCode(): String = Locale.getDefault().country
