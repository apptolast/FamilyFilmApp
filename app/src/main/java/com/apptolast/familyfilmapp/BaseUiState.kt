package com.apptolast.familyfilmapp

import com.apptolast.familyfilmapp.exceptions.CustomException

interface BaseUiState {
    val isLoading: Boolean
    val errorMessage: CustomException?

    fun copyWithLoading(isLoading: Boolean): BaseUiState
}
