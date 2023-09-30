package com.digitalsolution.familyfilmapp.ui.screens.profile

import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.model.local.UserData

data class ProfileUiState(
    val userData: UserData,
    val isLogged: Boolean,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState(isLoading, errorMessage) {

    constructor() : this(
        userData = UserData(email = "", pass = "", name = "", photo = ""),
        isLogged = false,
        isLoading = false,
        errorMessage = null,
    )
}
