package com.apptolast.familyfilmapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    val homeUiState: StateFlow<HomeUiState>
        field: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())

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
            triggerError("Empty List")
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
