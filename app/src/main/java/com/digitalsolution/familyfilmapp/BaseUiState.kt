package com.digitalsolution.familyfilmapp

import com.digitalsolution.familyfilmapp.exceptions.CustomException

open class BaseUiState(
    open val isLoading: Boolean,
    open val errorMessage: CustomException?,
)
