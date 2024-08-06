package com.apptolast.familyfilmapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.exceptions.HomeException
import com.apptolast.familyfilmapp.repositories.BackendRepository
import com.apptolast.familyfilmapp.repositories.LocalRepository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BackendRepository,
    private val localRepository: LocalRepository,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState

    init {
        getMovies()
    }

    private fun getMovies() = viewModelScope.launch(dispatcherProvider.io()) {
        repository.getMovies(1).fold(
            onSuccess = { movies ->
                _homeUiState.update { oldState ->
                    oldState.copy(
                        movies = movies,
                    )
                }
            },
            onFailure = {
                _homeUiState.update { oldState ->
                    oldState.copy(
                        errorMessage = HomeException.MovieException(),
                    )
                }
            },
        )
    }

    fun searchMovieByName(nameMovie: String) = viewModelScope.launch(dispatcherProvider.io()) {
        repository.searchMovieByName(1, nameMovie).fold(
            onSuccess = { movies ->
                _homeUiState.update { oldState ->
                    oldState.copy(
                        movies = movies,
                    )
                }
            },
            onFailure = {
                _homeUiState.update { oldState ->
                    oldState.copy(
                        errorMessage = HomeException.MovieException(),
                    )
                }
            },
        )
    }
}
