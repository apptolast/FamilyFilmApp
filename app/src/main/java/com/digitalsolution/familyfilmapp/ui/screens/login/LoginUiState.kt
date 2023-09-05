package com.digitalsolution.familyfilmapp.ui.screens.login

import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.model.local.UserData

data class LoginUiState(
    val screenState: LoginScreenState,
    val userData: UserData,
    val emailErrorMessage: String?,
    val passErrorMessage: String?,
    override val isLoading: Boolean,
    override val errorMessage: String?,
) : BaseUiState(isLoading, errorMessage) {

    constructor() : this(
        screenState = LoginScreenState.Login,
        UserData(
            email = "",
            pass = "",
            isLogin = false,
            isRegistered = false
        ),
        emailErrorMessage = null,
        passErrorMessage = null,
        isLoading = false,
        errorMessage = null,
    )
}

sealed class LoginScreenState(val message: String) {
    object Login : LoginScreenState("Login")
    object Register : LoginScreenState("Sign In")
}