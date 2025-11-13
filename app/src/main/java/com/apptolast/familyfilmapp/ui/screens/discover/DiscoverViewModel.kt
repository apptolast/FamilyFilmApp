package com.apptolast.familyfilmapp.ui.screens.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.types.MovieStatus
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Discover Screen
 * Handles movie discovery with swipe/button interactions
 */
@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val repository: Repository,
    private val auth: FirebaseAuth,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    val uiState: StateFlow<DiscoverUiState>
        field: MutableStateFlow<DiscoverUiState> = MutableStateFlow(DiscoverUiState())

    private var currentPage = 1

    init {
        loadUser()
        loadMovies()
    }

    /**
     * Load current user from repository
     */
    private fun loadUser() = viewModelScope.launch {
        val userId = auth.uid
        if (userId == null) {
            Timber.w("User not authenticated")
            triggerError("User not authenticated")
            return@launch
        }

        repository.getUserById(userId).collectLatest { user ->
            uiState.update { it.copy(user = user) }
        }
    }

    /**
     * Load popular movies, filtering out already marked ones
     */
    private fun loadMovies() = viewModelScope.launch(dispatcherProvider.io()) {
        try {
            uiState.update { it.copy(isLoading = true) }

            // Get popular movies from first page
            val popularMovies = repository.getPopularMoviesList(page = currentPage)
                .filter { movie ->
                    // Filter out movies already marked by user
                    val movieId = movie.id.toString()
                    !uiState.value.user.statusMovies.containsKey(movieId)
                }

            uiState.update {
                it.copy(
                    movies = popularMovies,
                    isLoading = false,
                    currentMovieIndex = 0,
                )
            }

            Timber.d("Loaded ${popularMovies.size} movies for discovery")
        } catch (e: Exception) {
            Timber.e(e, "Error loading movies")
            triggerError(e.message ?: "Error loading movies")
            uiState.update { it.copy(isLoading = false) }
        }
    }

    /**
     * Mark current movie as Watched and move to next
     */
    fun markAsWatched() = viewModelScope.launch(dispatcherProvider.io()) {
        val currentMovie = uiState.value.currentMovie ?: return@launch
        updateMovieStatus(currentMovie, MovieStatus.Watched)
        moveToNext()
    }

    /**
     * Mark current movie as Want to Watch and move to next
     */
    fun markAsWantToWatch() = viewModelScope.launch(dispatcherProvider.io()) {
        val currentMovie = uiState.value.currentMovie ?: return@launch
        updateMovieStatus(currentMovie, MovieStatus.ToWatch)
        moveToNext()
    }

    /**
     * Skip current movie without marking and move to next
     */
    fun skipMovie() {
        moveToNext()
    }

    /**
     * Move to the next movie in the list
     */
    private fun moveToNext() {
        uiState.update {
            it.copy(currentMovieIndex = it.currentMovieIndex + 1)
        }

        // If we're running low on movies, load more
        if (uiState.value.currentMovieIndex >= uiState.value.movies.size - 3) {
            loadMoreMovies()
        }
    }

    /**
     * Load more movies when running low
     */
    private fun loadMoreMovies() = viewModelScope.launch(dispatcherProvider.io()) {
        try {
            Timber.d("Loading more movies...")

            // Increment page and get next batch of popular movies
            currentPage++
            val newMovies = repository.getPopularMoviesList(page = currentPage)
                .filter { movie ->
                    val movieId = movie.id.toString()
                    !uiState.value.user.statusMovies.containsKey(movieId) &&
                        !uiState.value.movies.any { it.id == movie.id }
                }

            uiState.update {
                it.copy(movies = it.movies + newMovies)
            }

            Timber.d("Loaded ${newMovies.size} additional movies")
        } catch (e: Exception) {
            Timber.e(e, "Error loading more movies")
        }
    }

    /**
     * Update movie status in user's profile
     */
    private suspend fun updateMovieStatus(movie: Movie, status: MovieStatus) {
        try {
            val currentUser = uiState.value.user
            val movieId = movie.id.toString()

            // Update status map
            val updatedStatusMovies = currentUser.statusMovies.toMutableMap().apply {
                put(movieId, status)
            }

            // Create updated user
            val updatedUser = currentUser.copy(statusMovies = updatedStatusMovies)

            // Update in repository
            repository.updateUser(updatedUser)
        } catch (e: Exception) {
            Timber.e(e, "Error updating movie status")
            triggerError("Error updating movie status")
        }
    }

    /**
     * Trigger an error message
     */
    private fun triggerError(message: String) {
        uiState.update { it.copy(errorMessage = CustomException.GenericException(message)) }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        uiState.update { it.copy(errorMessage = null) }
    }
}
