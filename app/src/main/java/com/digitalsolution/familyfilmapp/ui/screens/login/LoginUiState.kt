package com.digitalsolution.familyfilmapp.ui.screens.login

import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.model.local.UserData

data class LoginUiState(
    var userData: UserData,
    override val isLoading: Boolean,
    override val hasError: Boolean,
    override val errorMessage: String
) : BaseUiState(isLoading, hasError, errorMessage) {

    constructor() : this(
        UserData(
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
