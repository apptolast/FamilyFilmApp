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
    val isLogged: Boolean,
    override val isLoading: Boolean,
    override val errorMessage: String?,
) : BaseUiState(isLoading, errorMessage) {

    constructor() : this(
        screenState = LoginScreenState.Login(),
        userData = UserData(
            email = "",
            pass = ""
        ),
        emailErrorMessage = null,
        passErrorMessage = null,
        isLogged = false,
        isLoading = false,
        errorMessage = null,
    )
}

sealed class LoginScreenState(
    @StringRes val buttonText: Int,
    @StringRes val accountText: Int,
    @StringRes val signText: Int
) {
    data class Login(
        @StringRes val button: Int = R.string.login_text_button,
        @StringRes val account: Int = R.string.login_text_no_account,
        @StringRes val sign: Int = R.string.login_text_sign_up
    ) : LoginScreenState(button, account, sign)

    data class Register(
        @StringRes val button: Int = R.string.register_text_button,
        @StringRes val account: Int = R.string.login_text_yes_account,
        @StringRes val sign: Int = R.string.login_text_sign_in
    ) : LoginScreenState(button, account, sign)
}