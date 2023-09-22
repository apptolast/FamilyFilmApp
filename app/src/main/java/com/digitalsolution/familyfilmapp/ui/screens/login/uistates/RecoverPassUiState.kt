package com.digitalsolution.familyfilmapp.ui.screens.login.uistates

import com.digitalsolution.familyfilmapp.exceptions.CustomException

data class RecoverPassUiState(
    val emailErrorMessage: CustomException?,
    val isSendEmailRecovered: Boolean,
    val errorMessage: CustomException?,
) {
    constructor() : this(
        emailErrorMessage = null,
        isSendEmailRecovered = false,
        errorMessage = null
    )
}
