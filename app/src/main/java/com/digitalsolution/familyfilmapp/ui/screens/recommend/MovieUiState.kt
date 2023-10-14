package com.digitalsolution.familyfilmapp.ui.screens.recommend

import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.model.local.Movie
import com.digitalsolution.familyfilmapp.model.local.User

data class MovieUiState(
    val user: User,
    val films: List<Movie>,
    val categories: List<String>,
    val isLogged: Boolean,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState(isLoading, errorMessage) {

    constructor() : this(
        user = User(email = "", pass = ""),
        films = emptyList(),
        categories = emptyList(),
        isLogged = false,
        isLoading = false,
        errorMessage = null,
    )
}
