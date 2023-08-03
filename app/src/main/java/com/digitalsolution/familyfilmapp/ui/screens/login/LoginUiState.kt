package com.digitalsolution.familyfilmapp.ui.screens.login

import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.model.local.Login

data class LoginUiState(
    var login: Login,
    override val isLoading: Boolean,
    override val hasError: Boolean,
    override val errorMessage: String
) : BaseUiState(isLoading, hasError, errorMessage) {

    constructor() : this(
        Login(
            email = "",
            pass = "",
            isLogin = false,
            isRegistered = false
        ),
        isLoading = false,
        hasError = false,
        errorMessage = ""
    )
}
