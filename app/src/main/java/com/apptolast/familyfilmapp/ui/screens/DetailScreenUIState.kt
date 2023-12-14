package com.apptolast.familyfilmapp.ui.screens

import com.apptolast.familyfilmapp.BaseUiState
import com.apptolast.familyfilmapp.exceptions.CustomException

data class DetailScreenUIState(
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,

    ) : BaseUiState {


    override fun copyWithLoading(isLoading: Boolean): BaseUiState {
        TODO("Not yet implemented")
    }

}
