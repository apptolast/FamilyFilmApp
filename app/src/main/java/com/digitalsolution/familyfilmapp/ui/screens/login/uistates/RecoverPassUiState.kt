package com.digitalsolution.familyfilmapp.ui.screens.login.uistates

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.exceptions.CustomException

data class RecoverPassUiState(
    val isDialogVisible: MutableState<Boolean>,
    val emailErrorMessage: CustomException?,
    val recoveryPassResponse: Boolean,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {

    constructor() : this(
        isDialogVisible = mutableStateOf(false),
        emailErrorMessage = null,
        recoveryPassResponse = false,
        isLoading = false,
        errorMessage = null,
    )
}
