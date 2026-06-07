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

// Manually composes the UTF-16 surrogate pair (Character.toChars is JVM-only).
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

fun countryCodeFromLanguageTag(languageTag: String, fallbackCountryCode: String = DEFAULT_COUNTRY_CODE): String {
    val normalizedFallback = fallbackCountryCode.normalizeCountryCode() ?: DEFAULT_COUNTRY_CODE
    val normalizedTag = languageTag.replace('_', '-')

    val matchingRegion = findRegionByLanguageTag(normalizedTag)?.countryCode
    if (matchingRegion != null) return matchingRegion

    val countryCode = normalizedTag
        .substringBefore("-u-", normalizedTag)
        .substringBefore("-x-", normalizedTag)
        .split("-")
        .drop(1)
        .firstNotNullOfOrNull { part -> part.normalizeCountryCode() }

    return countryCode ?: normalizedFallback
}

private fun String.normalizeCountryCode(): String? {
    val countryCode = trim().uppercase()
    return countryCode.takeIf { it.length == 2 && it.all { char -> char in 'A'..'Z' } }
}

private const val DEFAULT_COUNTRY_CODE = "US"

expect fun getCountryDisplayName(countryCode: String): String
