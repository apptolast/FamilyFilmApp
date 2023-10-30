package com.digitalsolution.familyfilmapp.ui.screens.groups.uistates

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.exceptions.CustomException

data class AddMemberUiState(
    // Representa la visibilidad del diálogo para pedir el correo
    val isDialogVisible: MutableState<Boolean>,

    // Mensaje de error específico para el campo de correo
    val emailErrorMessage: CustomException?,

    // Respuesta luego de intentar añadir al miembro. Podría ser un Booleano o algún otro tipo que represente el resultado
    val addMemberResponse: Boolean,

    // Si el proceso de añadir al miembro está cargando
    override val isLoading: Boolean,

    // Mensaje de error general del proceso
    override val errorMessage: CustomException?,

    ) : BaseUiState {
    constructor() : this(
        isDialogVisible = mutableStateOf(false),
        emailErrorMessage = null,
        addMemberResponse = false,
        isLoading = false,
        errorMessage = null,
    )
}
