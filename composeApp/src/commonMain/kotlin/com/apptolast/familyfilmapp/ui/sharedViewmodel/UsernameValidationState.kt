package com.apptolast.familyfilmapp.ui.sharedViewmodel

import com.apptolast.familyfilmapp.utils.UsernameValidator

sealed interface UsernameValidationState {
    data object Idle : UsernameValidationState
    data object Checking : UsernameValidationState
    data object Available : UsernameValidationState
    data object Taken : UsernameValidationState
    data class Invalid(val validationError: UsernameValidator.Result) : UsernameValidationState
}
