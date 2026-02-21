package com.apptolast.familyfilmapp.ui.screens.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.Group
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

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val repository: Repository,
    private val auth: FirebaseAuth,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    val uiState: StateFlow<DiscoverUiState>
        field: MutableStateFlow<DiscoverUiState> = MutableStateFlow(DiscoverUiState())

    private var currentPage = 1
    private var markedMovieIds: Set<Int> = emptySet()

    init {
        loadUser()
        loadGroups()
        loadMovies()
    }

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

    private fun loadGroups() = viewModelScope.launch {
        val userId = auth.uid ?: return@launch

        repository.getMyGroups(userId).collectLatest { groups ->
            uiState.update {
                it.copy(
                    groups = groups,
                    selectedGroupIds = if (it.selectedGroupIds.isEmpty()) {
                        groups.map { g -> g.id }.toSet()
                    } else {
                        it.selectedGroupIds
                    },
                )
            }
        }
    }

    private fun loadMovies() = viewModelScope.launch(dispatcherProvider.io()) {
        uiState.update { it.copy(isLoading = true) }

        // Load marked movie IDs for the user
        val userId = auth.uid
        if (userId != null) {
            markedMovieIds = try {
                repository.getAllMarkedMovieIdsForUser(userId).toSet()
            } catch (e: Exception) {
                Timber.e(e, "Error loading marked movie IDs")
                emptySet()
            }
        }

        repository.getPopularMoviesList(page = currentPage)
            .onSuccess { movies ->
                val popularMovies = movies.filter { movie ->
                    movie.id !in markedMovieIds
                }

                uiState.update {
                    it.copy(
                        movies = popularMovies,
                        isLoading = false,
                        currentMovieIndex = 0,
                    )
                }

                Timber.d("Loaded ${popularMovies.size} movies for discovery")
            }
            .onFailure { e ->
                Timber.e(e, "Error loading movies")
                triggerError(e.message ?: "Error loading movies")
                uiState.update { it.copy(isLoading = false) }
            }
    }

    fun toggleGroupSelection(groupId: String) {
        uiState.update {
            val updated = if (groupId in it.selectedGroupIds) {
                it.selectedGroupIds - groupId
            } else {
                it.selectedGroupIds + groupId
            }
            it.copy(selectedGroupIds = updated)
        }
    }

    fun markAsWatched() = viewModelScope.launch(dispatcherProvider.io()) {
        val currentMovie = uiState.value.currentMovie ?: return@launch
        updateMovieStatus(currentMovie, MovieStatus.Watched)
        moveToNext()
    }

    fun markAsWantToWatch() = viewModelScope.launch(dispatcherProvider.io()) {
        val currentMovie = uiState.value.currentMovie ?: return@launch
        updateMovieStatus(currentMovie, MovieStatus.ToWatch)
        moveToNext()
    }

    fun skipMovie() {
        moveToNext()
    }

    private fun moveToNext() {
        uiState.update {
            it.copy(currentMovieIndex = it.currentMovieIndex + 1)
        }

        if (uiState.value.currentMovieIndex >= uiState.value.movies.size - 3) {
            loadMoreMovies()
        }
    }

    private fun loadMoreMovies() = viewModelScope.launch(dispatcherProvider.io()) {
        Timber.d("Loading more movies...")

        currentPage++
        repository.getPopularMoviesList(page = currentPage)
            .onSuccess { movies ->
                val newMovies = movies.filter { movie ->
                    movie.id !in markedMovieIds &&
                        !uiState.value.movies.any { it.id == movie.id }
                }

                uiState.update {
                    it.copy(movies = it.movies + newMovies)
                }

                Timber.d("Loaded ${newMovies.size} additional movies")
            }
            .onFailure { e ->
                Timber.e(e, "Error loading more movies")
            }
    }

    private suspend fun updateMovieStatus(movie: Movie, status: MovieStatus) {
        try {
            val userId = auth.uid ?: return
            val selectedGroups = uiState.value.selectedGroupIds.toList()

            if (selectedGroups.isEmpty()) {
                triggerError("Select at least one group")
                return
            }

            repository.updateMovieStatus(selectedGroups, userId, movie.id, status)
                .onSuccess {
                    markedMovieIds = markedMovieIds + movie.id
                    Timber.d("Movie ${movie.title} marked as $status in ${selectedGroups.size} groups")
                }
                .onFailure { e ->
                    Timber.e(e, "Error updating movie status")
                    triggerError("Error updating movie status")
                }
        } catch (e: Exception) {
            Timber.e(e, "Error updating movie status")
            triggerError("Error updating movie status")
        }
    }

    private fun triggerError(message: String) {
        uiState.update { it.copy(errorMessage = CustomException.GenericException(message)) }
    }

    fun clearError() {
        uiState.update { it.copy(errorMessage = null) }
    }
}
