package com.digitalsolution.familyfilmapp.ui.screens.recommend.states

import com.digitalsolution.familyfilmapp.BaseUiState
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.model.local.Movie
import com.digitalsolution.familyfilmapp.model.local.User

data class MovieUiState(
    val user: User,
    val movies: List<Movie>,
    val categories: List<String>,
    val isLogged: Boolean,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {

    constructor() : this(
        user = User(email = "", pass = "", name = "", photo = ""),
        movies = emptyList(),
        categories = emptyList(),
        isLogged = false,
        isLoading = false,
        errorMessage = null,
    )

    override fun copyWithLoading(isLoading: Boolean): BaseUiState = this.copy(isLoading = isLoading)
}
