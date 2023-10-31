package com.digitalsolution.familyfilmapp.ui.screens.groups.uistates

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.exceptions.CustomException

data class AddMemberUiState(
    val isBottomSheetVisible: MutableState<Boolean>,

    val emailErrorMessage: CustomException?,

    val addMemberResponse: Boolean,

    override val isLoading: Boolean,

    override val errorMessage: CustomException?,

    ) : BaseUiState {
    constructor() : this(
        isBottomSheetVisible = mutableStateOf(false),
        emailErrorMessage = null,
        addMemberResponse = false,
        isLoading = false,
        errorMessage = null,
    )
}
