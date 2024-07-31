package com.apptolast.familyfilmapp.ui.screens.home

import com.apptolast.familyfilmapp.BaseUiState
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.MovieCatalogue

data class HomeUiState(
    val movies: List<MovieCatalogue>,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {

    constructor() : this(
        movies = emptyList(),
        isLoading = false,
        errorMessage = null,
    )

    override fun copyWithLoading(isLoading: Boolean): BaseUiState = this.copy(
        isLoading = isLoading,
    )
}
