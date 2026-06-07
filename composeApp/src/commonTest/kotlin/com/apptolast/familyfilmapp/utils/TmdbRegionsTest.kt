package com.apptolast.familyfilmapp.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class TmdbRegionsTest {

    @Test
    fun countryCodeFromLanguageTag_handlesUnicodeLocaleExtensions() {
        assertEquals("GB", countryCodeFromLanguageTag("en-GB-u-mu-celsius"))
    }

    @Test
    fun countryCodeFromLanguageTag_handlesSimpleTags() {
        assertEquals("ES", countryCodeFromLanguageTag("es-ES"))
    }

    @Test
    fun countryCodeFromLanguageTag_usesFallbackWhenRegionIsMissing() {
        assertEquals("FR", countryCodeFromLanguageTag("en", fallbackCountryCode = "FR"))
    }

    @Test
    fun countryCodeFromLanguageTag_usesDefaultWhenFallbackIsInvalid() {
        assertEquals("US", countryCodeFromLanguageTag("en", fallbackCountryCode = "GB-u-mu-celsius"))
    }
}
