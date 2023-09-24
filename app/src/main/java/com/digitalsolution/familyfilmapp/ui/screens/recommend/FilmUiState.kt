package com.digitalsolution.familyfilmapp.ui.screens.recommend

import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.model.local.FilmData
import com.digitalsolution.familyfilmapp.model.local.UserData

data class FilmUiState(
    val userData: UserData,
    val films: List<FilmData>,
    val categories: List<String>,
    val isLogged: Boolean,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState(isLoading, errorMessage) {

    constructor() : this(
        userData = UserData(email = "", pass = ""),
        films = emptyList(),
        categories = emptyList(),
        isLogged = false,
        isLoading = false,
        errorMessage = null,
    )
}
