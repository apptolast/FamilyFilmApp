package com.digitalsolution.familyfilmapp.ui.screens.login.usecases

import java.util.regex.Pattern

private const val PASSWORD_VALIDATION_REGEX =
    "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}\$"


fun String.isPasswordValid(): Boolean =
    (Pattern.compile(PASSWORD_VALIDATION_REGEX).matcher(this).matches())
