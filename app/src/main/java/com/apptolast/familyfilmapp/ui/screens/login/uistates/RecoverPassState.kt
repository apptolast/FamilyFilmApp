package com.apptolast.familyfilmapp.ui.screens.login.uistates

data class RecoverPassState(
    val isDialogVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccessful: Boolean = false,
    val email: String = "",
    val emailErrorMessage: String? = null,
    val errorMessage: String? = null
)
