package com.apptolast.familyfilmapp.ui.screens.home

import com.apptolast.familyfilmapp.BaseUiState
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User

data class HomeUiState(
    val user: User,
    val filterMovies: List<Movie>,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {

    constructor() : this(
        user = User(),
        filterMovies = emptyList(),
        isLoading = false,
        errorMessage = null,
    )
}
