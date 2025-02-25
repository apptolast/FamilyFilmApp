package com.apptolast.familyfilmapp.ui.screens.login.uistates

import com.apptolast.familyfilmapp.exceptions.CustomException

data class LoginState(
    val screenState: LoginRegisterState,
    val email: String,
    val emailErrorMessage: CustomException?,
    val passErrorMessage: CustomException?,
    val isLogged: Boolean,
    val isEmailVerified: Boolean,
    val isLoading: Boolean,
    val errorMessage: CustomException?,
) {

    constructor() : this(
        screenState = LoginRegisterState.Login(),
        email = "",
        emailErrorMessage = null,
        passErrorMessage = null,
        isLogged = false,
        isEmailVerified = false,
        isLoading = false,
        errorMessage = null,
    )
}
