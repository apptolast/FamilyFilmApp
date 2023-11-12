package com.digitalsolution.familyfilmapp.extensions

import android.icu.text.SimpleDateFormat
import android.util.Base64
import android.util.Patterns
import org.json.JSONObject
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

fun String.isEmailValid(): Boolean = Patterns.EMAIL_ADDRESS.matcher(this).matches()

private const val PASSWORD_VALIDATION_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}\$"

fun String.isPasswordValid(): Boolean = Pattern.compile(PASSWORD_VALIDATION_REGEX).matcher(this).matches()

fun String.toDate(format: String = "yyyy-MM-dd"): Date = SimpleDateFormat(format, Locale.getDefault()).parse(this)

fun String.decodeJWT(): JSONObject {
    val splitToken = this.split(".")
    val base64EncodedBody = splitToken[1]
    val decodedString = Base64.decode(base64EncodedBody, Base64.URL_SAFE).decodeToString()
    return JSONObject(decodedString)
}
