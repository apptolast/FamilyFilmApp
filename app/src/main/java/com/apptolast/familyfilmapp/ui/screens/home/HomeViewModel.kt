package com.apptolast.familyfilmapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.exceptions.HomeException
import com.apptolast.familyfilmapp.repositories.BackendRepository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BackendRepository,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        getMovies()
    }

    private fun getMovies() = viewModelScope.launch(dispatcherProvider.io()) {
        repository.getMovies(1).fold(
            onSuccess = { movies ->
                _uiState.update { oldState ->
                    oldState.copy(
                        movies = movies,
                    )
                }
            },
            onFailure = {
                _uiState.update { oldState ->
                    oldState.copy(
                        errorMessage = HomeException.MovieException(),
                    )
                }
            },
        )
    }

    fun searchMovieByName(movieName: String) = viewModelScope.launch(dispatcherProvider.io()) {
        repository.searchMovieByName(1, movieName).fold(
            onSuccess = { movies ->
                print("Movies : $movies")
                _uiState.update { oldState ->
                    oldState.copy(
                        movies = movies,
                    )
                }
            },
            onFailure = { error ->
                Timber.e("Error: ${error.message}")
                _uiState.update { oldState ->
                    oldState.copy(
                        errorMessage = HomeException.MovieException(error.message!!),
                    )
                }
            },
        )
    }
}
