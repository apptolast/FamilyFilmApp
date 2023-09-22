package com.digitalsolution.familyfilmapp.ui.screens.login

import androidx.annotation.StringRes
import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.model.local.UserData

data class LoginUiState(
    val screenState: LoginScreenState,
    val userData: UserData,
    val emailErrorMessage: CustomException?,
    val passErrorMessage: CustomException?,
    val isLogged: Boolean,
    val isSendEmailRecovered: Boolean,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
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
        isSendEmailRecovered = false,
        isLoading = false,
        errorMessage = null,
    )
}

data class RecoverPassUIState(
    val emailErrorMessage: CustomException?,
    val isSendEmailRecovered: Boolean
)

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

