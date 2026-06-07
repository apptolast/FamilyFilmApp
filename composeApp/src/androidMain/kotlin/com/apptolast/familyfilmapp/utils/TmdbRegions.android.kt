package com.apptolast.familyfilmapp.utils

import java.util.Locale

actual fun getCountryDisplayName(countryCode: String): String = runCatching {
    Locale.Builder().setRegion(countryCode).build().getDisplayCountry(Locale.getDefault())
}.getOrElse {
    Locale.Builder().setRegion("US").build().getDisplayCountry(Locale.getDefault())
}
