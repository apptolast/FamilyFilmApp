package com.apptolast.familyfilmapp.ui.sharedViewmodel

sealed interface UsernameValidationState {
    data object Idle : UsernameValidationState
    data object Checking : UsernameValidationState
    data object Available : UsernameValidationState
    data object Taken : UsernameValidationState
    data class Invalid(val reason: String) : UsernameValidationState
}
