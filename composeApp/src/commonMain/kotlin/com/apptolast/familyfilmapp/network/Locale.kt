package com.apptolast.familyfilmapp.network

/** BCP 47 language tag for the user's current locale (e.g. "es-ES"). */
expect fun systemLanguageTag(): String

/** ISO 3166-1 alpha-2 country code for the user's current locale (e.g. "ES"). */
expect fun systemCountryCode(): String
