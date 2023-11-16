package com.digitalsolution.familyfilmapp.ui.screens.profile

import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.model.local.User

data class ProfileUiState(
    val userData: User,
    val isLogged: Boolean,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {

    constructor() : this(
        userData = User(email = "", pass = "", name = "", photo = ""),
        isLogged = false,
        isLoading = false,
        errorMessage = null,
    )

    override fun copyWithLoading(isLoading: Boolean): BaseUiState = this.copy(isLoading = isLoading)
}
