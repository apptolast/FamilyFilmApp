package com.digitalsolution.familyfilmapp.ui.screens.login.uistates

import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.model.local.User

data class LoginUiState(
    val screenState: LoginRegisterState,
    val user: User,
    val emailErrorMessage: CustomException?,
    val passErrorMessage: CustomException?,
    val isLogged: Boolean,
    val isSendEmailRecovered: Boolean,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {

    constructor() : this(
        screenState = LoginRegisterState.Login(),
        user = User(
            email = "",
            pass = "",
            name = "",
            photo = "",
        ),
        emailErrorMessage = null,
        passErrorMessage = null,
        isLogged = false,
        isSendEmailRecovered = false,
        isLoading = false,
        errorMessage = null,
    )
}
