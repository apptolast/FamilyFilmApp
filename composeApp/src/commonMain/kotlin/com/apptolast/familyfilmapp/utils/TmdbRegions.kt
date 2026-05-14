package com.apptolast.familyfilmapp.utils

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

/**
 * Returns the regional indicator emoji for an ISO 3166-1 alpha-2 country code
 * (e.g. "US" → 🇺🇸). Built by manually composing the UTF-16 surrogate pair
 * for each Regional Indicator Symbol — `Character.toChars()` from the legacy
 * implementation is JVM-only.
 */
fun countryCodeToFlag(countryCode: String): String {
    if (countryCode.length != 2) return ""
    val first = regionalIndicator(countryCode[0])
    val second = regionalIndicator(countryCode[1])
    return first + second
}

private fun regionalIndicator(letter: Char): String {
    val codePoint = 0x1F1E6 + (letter.uppercaseChar() - 'A')
    val offset = codePoint - 0x10000
    val high = (0xD800 + (offset shr 10)).toChar()
    val low = (0xDC00 + (offset and 0x3FF)).toChar()
    return "$high$low"
}

fun findRegionByLanguageTag(languageTag: String): TmdbRegion? =
    TMDB_REGIONS.find { it.languageTag.equals(languageTag, ignoreCase = true) }

/**
 * Localised display name for a country code (e.g. "ES" → "España" when the
 * user locale is Spanish). Backed by `java.util.Locale` on Android and
 * `NSLocale` on iOS — see the actuals in androidMain/iosMain.
 */
expect fun getCountryDisplayName(countryCode: String): String
