package com.digitalsolution.familyfilmapp

open class BaseUiState(
    open val isLoading: Boolean,
    open val hasError: Boolean,
    open val errorMessage: String,
)
