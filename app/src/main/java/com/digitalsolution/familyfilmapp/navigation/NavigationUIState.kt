package com.digitalsolution.familyfilmapp.navigation

import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.exceptions.CustomException

data class NavigationUIState(
    val isUserLoggedIn: Boolean? = null,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {
    constructor() : this(
        isUserLoggedIn = null,
        isLoading = false,
        errorMessage = null,
    )

    override fun copyWithLoading(isLoading: Boolean): BaseUiState = this.copy(isLoading = isLoading)
}
