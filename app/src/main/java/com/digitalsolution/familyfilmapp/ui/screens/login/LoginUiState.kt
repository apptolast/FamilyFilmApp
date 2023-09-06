package com.digitalsolution.familyfilmapp.ui.screens.login

import androidx.annotation.StringRes
import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.R
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
        screenState = LoginScreenState.Login(),
        userData = UserData(
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

sealed class LoginScreenState(@StringRes val buttonText: Int) {
    data class Login(@StringRes val value: Int = R.string.login_text_button) : LoginScreenState(value)
    data class Register(@StringRes val value: Int = R.string.register_text_button) : LoginScreenState(value)
}