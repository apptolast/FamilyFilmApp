package com.apptolast.familyfilmapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.ui.sharedViewmodel.UsernameValidationState

object UsernameValidator {

    private val VALID_PATTERN = Regex("^[a-zA-Z][a-zA-Z0-9_.]{2,19}$")

    sealed class Result {
        data object Valid : Result()
        data object TooShort : Result()
        data object TooLong : Result()
        data object InvalidChars : Result()
        data object MustStartWithLetter : Result()
    }

    fun validate(username: String): Result = when {
        username.length < 3 -> Result.TooShort
        username.length > 20 -> Result.TooLong
        !username[0].isLetter() -> Result.MustStartWithLetter
        !VALID_PATTERN.matches(username) -> Result.InvalidChars
        else -> Result.Valid
    }

    /**
     * Converts a validation result to [UsernameValidationState].
     * Returns null for [Result.Valid] (caller should proceed to availability check)
     * and [UsernameValidationState.Idle] for [Result.TooShort] (not enough input yet).
     */
    fun Result.toValidationState(): UsernameValidationState? = when (this) {
        is Result.Valid -> null
        is Result.TooShort -> UsernameValidationState.Idle
        else -> UsernameValidationState.Invalid(this)
    }
}

@Composable
fun UsernameValidator.Result.toErrorString(): String = when (this) {
    is UsernameValidator.Result.TooLong -> stringResource(R.string.username_too_long)
    is UsernameValidator.Result.InvalidChars -> stringResource(R.string.username_invalid_format)
    is UsernameValidator.Result.MustStartWithLetter -> stringResource(R.string.username_must_start_with_letter)
    is UsernameValidator.Result.TooShort -> stringResource(R.string.username_too_short)
    is UsernameValidator.Result.Valid -> ""
}
