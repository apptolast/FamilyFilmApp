package com.digitalsolution.familyfilmapp.extensions

import android.icu.text.SimpleDateFormat
import android.util.Patterns
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

fun String.isEmailValid(): Boolean = Patterns.EMAIL_ADDRESS.matcher(this).matches()

private const val PASSWORD_VALIDATION_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}\$"

fun String.isPasswordValid(): Boolean = Pattern.compile(PASSWORD_VALIDATION_REGEX).matcher(this).matches()

fun String.toDate(format: String = "yyyy-MM-dd"): Date = SimpleDateFormat(format, Locale.getDefault()).parse(this)
