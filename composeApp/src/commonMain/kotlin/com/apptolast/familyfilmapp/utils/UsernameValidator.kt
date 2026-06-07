package com.apptolast.familyfilmapp.utils

import androidx.compose.runtime.Composable
import com.apptolast.familyfilmapp.ui.sharedViewmodel.UsernameValidationState
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.username_invalid_format
import familyfilmkmp.composeapp.generated.resources.username_must_start_with_letter
import familyfilmkmp.composeapp.generated.resources.username_too_long
import familyfilmkmp.composeapp.generated.resources.username_too_short
import org.jetbrains.compose.resources.stringResource

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

    // Returns null for Valid (caller proceeds to availability check) and Idle for TooShort.
    fun Result.toValidationState(): UsernameValidationState? = when (this) {
        is Result.Valid -> null
        is Result.TooShort -> UsernameValidationState.Idle
        else -> UsernameValidationState.Invalid(this)
    }
}

@Composable
fun UsernameValidator.Result.toErrorString(): String = when (this) {
    is UsernameValidator.Result.TooLong -> stringResource(Res.string.username_too_long)
    is UsernameValidator.Result.InvalidChars -> stringResource(Res.string.username_invalid_format)
    is UsernameValidator.Result.MustStartWithLetter -> stringResource(Res.string.username_must_start_with_letter)
    is UsernameValidator.Result.TooShort -> stringResource(Res.string.username_too_short)
    is UsernameValidator.Result.Valid -> ""
}
