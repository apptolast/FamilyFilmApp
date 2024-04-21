package com.apptolast.familyfilmapp.ui.screens.login.uistates

import com.apptolast.familyfilmapp.BaseUiState
import com.apptolast.familyfilmapp.exceptions.CustomException

data class RecoverPassState(
    val isDialogVisible: Boolean,
    val emailErrorMessage: CustomException?,
    val recoveryPassResponse: Boolean,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {

    constructor() : this(
        isDialogVisible = false,
        emailErrorMessage = null,
        recoveryPassResponse = false,
        isLoading = false,
        errorMessage = null,
    )

    override fun copyWithLoading(isLoading: Boolean): BaseUiState = this.copy(
        isLoading = isLoading,
    )
}
