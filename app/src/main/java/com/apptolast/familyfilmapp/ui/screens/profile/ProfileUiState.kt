package com.apptolast.familyfilmapp.ui.screens.profile

import com.apptolast.familyfilmapp.BaseUiState
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.User

data class ProfileUiState(
    val userData: User,
    val isLogged: Boolean,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {

    constructor() : this(
        userData = User(),
        isLogged = false,
        isLoading = false,
        errorMessage = null,
    )

    override fun copyWithLoading(isLoading: Boolean): BaseUiState = this.copy(
        isLoading = isLoading,
    )
}
