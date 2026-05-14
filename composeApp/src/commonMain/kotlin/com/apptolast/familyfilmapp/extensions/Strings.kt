package com.apptolast.familyfilmapp.extensions

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

// Multiplatform replacements for the legacy Android-only extensions in
// app/src/main/java/.../extensions/Strings.kt (which relied on
// android.util.Patterns, android.util.Base64, android.icu.text.SimpleDateFormat
// and org.json.JSONObject — none of which exist outside the Android target).

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

private val PASSWORD_REGEX = Regex("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")

fun String.isEmailValid(): Boolean = EMAIL_REGEX.matches(this)

fun String.isPasswordValid(): Boolean = PASSWORD_REGEX.matches(this)

/**
 * Decodes the payload (middle segment) of a JWT and parses it as a JsonObject.
 * Mirrors the legacy `decodeJWT()` but uses the Kotlin stdlib URL-safe Base64
 * decoder and kotlinx-serialization instead of android.util.Base64 + JSONObject.
 */
@OptIn(ExperimentalEncodingApi::class)
fun String.decodeJwtPayload(): JsonObject {
    val payload = split(".").getOrNull(1) ?: error("Malformed JWT: expected 3 segments")
    val decoded = Base64.UrlSafe.decode(payload).decodeToString()
    return Json.parseToJsonElement(decoded) as JsonObject
}
