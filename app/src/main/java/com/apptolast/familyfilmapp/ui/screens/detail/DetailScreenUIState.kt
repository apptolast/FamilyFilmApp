package com.apptolast.familyfilmapp.ui.screens.detail

import com.apptolast.familyfilmapp.BaseUiState
import com.apptolast.familyfilmapp.exceptions.CustomException

data class DetailScreenUIState(
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,

) : BaseUiState {

    constructor() : this(
        isLoading = false,
        errorMessage = null,
    )
}
