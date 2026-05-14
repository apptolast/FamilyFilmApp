package com.apptolast.familyfilmapp.utils

import java.util.Locale

actual fun getCountryDisplayName(countryCode: String): String =
    Locale.Builder().setRegion(countryCode).build().getDisplayCountry(Locale.getDefault())
