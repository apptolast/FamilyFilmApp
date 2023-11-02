package com.digitalsolution.familyfilmapp.ui.screens.groups.uistates

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.exceptions.CustomException

data class AddMemberUiState(
    val isBottomSheetVisible: MutableState<Boolean>,

    val email: MutableState<String>,

    val emailErrorMessage: CustomException?,

    val showSnackbar: MutableState<Boolean>,

    override val isLoading: Boolean,

    override val errorMessage: CustomException?,

    ) : BaseUiState {
    constructor() : this(
        isBottomSheetVisible = mutableStateOf(false),
        email = mutableStateOf(""),
        emailErrorMessage = null,
        showSnackbar = mutableStateOf(false),
        isLoading = false,
        errorMessage = null,
    )
}
