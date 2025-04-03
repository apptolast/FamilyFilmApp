package com.apptolast.familyfilmapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    private val auth: FirebaseAuth,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    val homeUiState: StateFlow<HomeUiState>
        field: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())

    init {
        viewModelScope.launch {
            homeUiState.update { it.copy(isLoading = true) }
            repository.getUserById(auth.uid!!).collectLatest { user ->
                homeUiState.update {
                    it.copy(
                        isLoading = false,
                        user = user,
                    )
                }
            }
        }
    }

    val movies = repository.getPopularMovies()
        .catch { error ->
            triggerError(error.message ?: "Error getting movies")
            Timber.e(error, "Error getting movies")
        }
        .distinctUntilChanged()
        .cachedIn(viewModelScope)

    fun searchMovieByName(movieFilter: String) = viewModelScope.launch(dispatcherProvider.io()) {
        if (movieFilter.isEmpty()) {
            homeUiState.update { it.copy(filterMovies = emptyList()) }
            triggerError("Empty List") // Todo: Fix the hardcoded message and its test
            Timber.e("Empty List")
        } else {
            repository.searchTmdbMovieByName(movieFilter).let { movies ->
                homeUiState.update { it.copy(filterMovies = movies) }
            }
        }
    }

    fun triggerError(errorMessage: String) {
        homeUiState.update { it.copy(errorMessage = CustomException.GenericException(errorMessage)) }
    }

    fun clearError() {
        homeUiState.update { it.copy(errorMessage = CustomException.GenericException(null)) }
    }
}
