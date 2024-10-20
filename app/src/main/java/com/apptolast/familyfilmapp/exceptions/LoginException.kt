package com.apptolast.familyfilmapp.exceptions

sealed interface LoginException : CustomException {

    data class EmailInvalidFormat(override val error: String = "Email format not valid") : LoginException

    data class PasswordInvalidFormat(
        override val error: String = """
            Password must contain special chars, numbers, lower and upper case,
            and more than 8 characters
        """.trimIndent(),
    ) : LoginException

    data class BackendLogin(override val error: String = "Backend login failed") : LoginException
}
