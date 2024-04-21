package com.apptolast.familyfilmapp.ui.screens.recommend.states

import com.apptolast.familyfilmapp.BaseUiState
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User

data class MovieUiState(
    val user: User,
    val movies: List<Movie>,
    val categories: List<String>,
    val isLogged: Boolean,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {

    constructor() : this(
        user = User(id = "", email = "", pass = "", name = "", photo = ""),
        movies = emptyList(),
        categories = emptyList(),
        isLogged = false,
        isLoading = false,
        errorMessage = null,
    )

    override fun copyWithLoading(isLoading: Boolean): BaseUiState = this.copy(
        isLoading = isLoading,
    )
}
