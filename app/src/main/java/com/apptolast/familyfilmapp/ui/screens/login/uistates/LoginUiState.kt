package com.apptolast.familyfilmapp.ui.screens.login.uistates

import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.User

data class LoginUiState(
    val screenState: LoginRegisterState,
    val user: User,
    val emailErrorMessage: CustomException?,
    val passErrorMessage: CustomException?,
    val isLogged: Boolean,
    val isLoading: Boolean,
    val errorMessage: CustomException?,
) {

    constructor() : this(
        screenState = LoginRegisterState.Login(),
        user = User(
            id = "",
            email = "",
            pass = "",
            name = "",
            photo = "",
//            token = "",
        ),
        emailErrorMessage = null,
        passErrorMessage = null,
        isLogged = false,
        isLoading = false,
        errorMessage = null,
    )
}
