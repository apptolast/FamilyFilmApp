package com.apptolast.familyfilmapp.ui.screens.discover

import com.apptolast.familyfilmapp.BaseUiState
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User

/**
 * UI State for Discover Screen
 *
 * @param user Current logged-in user
 * @param movies List of movies to discover (popular movies not yet marked)
 * @param currentMovieIndex Index of the current movie being shown
 * @param isLoading Loading state
 * @param errorMessage Error message if any
 */
data class DiscoverUiState(
    val user: User,
    val movies: List<Movie>,
    val currentMovieIndex: Int,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState {

    constructor() : this(
        user = User(),
        movies = emptyList(),
        currentMovieIndex = 0,
        isLoading = false,
        errorMessage = null,
    )

    /**
     * Get the current movie being displayed
     */
    val currentMovie: Movie?
        get() = movies.getOrNull(currentMovieIndex)

    /**
     * Check if there are more movies to show
     */
    val hasMoreMovies: Boolean
        get() = currentMovieIndex < movies.size - 1

    /**
     * Check if we've run out of movies
     */
    val isOutOfMovies: Boolean
        get() = movies.isEmpty() || currentMovieIndex >= movies.size
}
