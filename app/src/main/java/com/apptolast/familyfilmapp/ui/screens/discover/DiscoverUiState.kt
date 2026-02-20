package com.apptolast.familyfilmapp.ui.screens.discover

import com.apptolast.familyfilmapp.BaseUiState
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User

data class DiscoverUiState(
    val user: User,
    val movies: List<Movie>,
    val currentMovieIndex: Int,
    val groups: List<Group>,
    val selectedGroupIds: Set<String>,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {

    constructor() : this(
        user = User(),
        movies = emptyList(),
        currentMovieIndex = 0,
        groups = emptyList(),
        selectedGroupIds = emptySet(),
        isLoading = false,
        errorMessage = null,
    )

    val currentMovie: Movie?
        get() = movies.getOrNull(currentMovieIndex)

    val hasMoreMovies: Boolean
        get() = currentMovieIndex < movies.size - 1

    val isOutOfMovies: Boolean
        get() = movies.isEmpty() || currentMovieIndex >= movies.size
}
