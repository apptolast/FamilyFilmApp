package com.apptolast.familyfilmapp.utils

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
}
