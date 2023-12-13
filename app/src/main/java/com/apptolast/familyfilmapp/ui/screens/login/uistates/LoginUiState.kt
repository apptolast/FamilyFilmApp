package com.apptolast.familyfilmapp.ui.screens.login.uistates

import com.apptolast.familyfilmapp.BaseUiState
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.User

data class LoginUiState(
    val screenState: LoginRegisterState,
    val user: User,
    val emailErrorMessage: CustomException?,
    val passErrorMessage: CustomException?,
    val isLogged: Boolean,
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
        isLoading = false,
        errorMessage = null,
    )

    override fun copyWithLoading(isLoading: Boolean): BaseUiState = this.copy(
        isLoading = isLoading,
    )
}
