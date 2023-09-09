package com.digitalsolution.familyfilmapp.ui.screens.login.usecases

import android.util.Patterns
import java.util.regex.Pattern

fun String.isEmailValid(): Boolean =
    Patterns.EMAIL_ADDRESS.matcher(this).matches()

private const val PasswordValidationRegex =
    "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}\$"

fun String.isPasswordValid(): Boolean =
    Pattern.compile(PasswordValidationRegex).matcher(this).matches()
