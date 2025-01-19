package com.apptolast.familyfilmapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    val homeUiState: StateFlow<HomeUiState>
        field: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())

    init {
        getMovies()
    }

    private fun getMovies() = viewModelScope.launch(dispatcherProvider.io()) {
        repository.getPopularMovies().let {
            homeUiState.update { oldState ->
                oldState.copy(
                    movies = it,
                )
            }
        }
    }

    fun searchMovieByName(movieName: String) = viewModelScope.launch(dispatcherProvider.io()) {
//        repository.searchMovieByName(1, movieName).fold(
//            onSuccess = { movies ->
//                print("Movies : $movies")
//                _homeUiState.update { oldState ->
//                    oldState.copy(
//                        movies = movies,
//                    )
//                }
//            },
//            onFailure = { error ->
//                Timber.e("Error: ${error.message}")
//                _homeUiState.update { oldState ->
//                    oldState.copy(
//                        errorMessage = HomeException.MovieException(error.message!!),
//                    )
//                }
//            },
//        )
    }
}
