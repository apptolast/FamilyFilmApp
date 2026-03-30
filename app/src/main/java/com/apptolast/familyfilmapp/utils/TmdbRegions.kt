package com.apptolast.familyfilmapp.utils

import java.util.Locale

data class TmdbRegion(val countryCode: String, val languageTag: String)

/**
 * Curated list of TMDB-supported regions.
 * Each entry maps a country code (ISO 3166-1 alpha-2) to its primary TMDB language tag.
 */
val TMDB_REGIONS: List<TmdbRegion> = listOf(
    TmdbRegion("AR", "es-AR"),
    TmdbRegion("AU", "en-AU"),
    TmdbRegion("AT", "de-AT"),
    TmdbRegion("BE", "fr-BE"),
    TmdbRegion("BR", "pt-BR"),
    TmdbRegion("CA", "en-CA"),
    TmdbRegion("CL", "es-CL"),
    TmdbRegion("CN", "zh-CN"),
    TmdbRegion("CO", "es-CO"),
    TmdbRegion("CZ", "cs-CZ"),
    TmdbRegion("DK", "da-DK"),
    TmdbRegion("FI", "fi-FI"),
    TmdbRegion("FR", "fr-FR"),
    TmdbRegion("DE", "de-DE"),
    TmdbRegion("GR", "el-GR"),
    TmdbRegion("HK", "zh-HK"),
    TmdbRegion("IN", "hi-IN"),
    TmdbRegion("IE", "en-IE"),
    TmdbRegion("IT", "it-IT"),
    TmdbRegion("JP", "ja-JP"),
    TmdbRegion("KR", "ko-KR"),
    TmdbRegion("MX", "es-MX"),
    TmdbRegion("NL", "nl-NL"),
    TmdbRegion("NZ", "en-NZ"),
    TmdbRegion("NO", "nb-NO"),
    TmdbRegion("PE", "es-PE"),
    TmdbRegion("PL", "pl-PL"),
    TmdbRegion("PT", "pt-PT"),
    TmdbRegion("RO", "ro-RO"),
    TmdbRegion("RU", "ru-RU"),
    TmdbRegion("ES", "es-ES"),
    TmdbRegion("SE", "sv-SE"),
    TmdbRegion("CH", "de-CH"),
    TmdbRegion("TW", "zh-TW"),
    TmdbRegion("TR", "tr-TR"),
    TmdbRegion("GB", "en-GB"),
    TmdbRegion("US", "en-US"),
    TmdbRegion("VE", "es-VE"),
)

fun countryCodeToFlag(countryCode: String): String {
    if (countryCode.length != 2) return ""
    val first = Character.toChars(0x1F1E6 + (countryCode[0].uppercaseChar() - 'A'))
    val second = Character.toChars(0x1F1E6 + (countryCode[1].uppercaseChar() - 'A'))
    return String(first) + String(second)
}

fun findRegionByLanguageTag(languageTag: String): TmdbRegion? =
    TMDB_REGIONS.find { it.languageTag.equals(languageTag, ignoreCase = true) }

fun getCountryDisplayName(countryCode: String): String =
    Locale.Builder().setRegion(countryCode).build().getDisplayCountry(Locale.getDefault())
