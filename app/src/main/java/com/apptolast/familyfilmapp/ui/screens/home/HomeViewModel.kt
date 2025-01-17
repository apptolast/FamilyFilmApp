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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BackendRepository,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        getMovies()
    }

    private fun getMovies(page: Int = 1) = viewModelScope.launch(dispatcherProvider.io()) {
        repository.getMovies(page).fold(
            onSuccess = { movies ->
                _state.update { oldState ->
                    oldState.copy(
                        movies = movies,
                    )
                }
            },
            onFailure = { error ->
                Timber.e(error)
                _state.update { oldState ->
                    oldState.copy(
                        errorMessage = HomeException.MovieException(),
                    )
                }
            },
        )
    }

//    fun searchMovieByName(movieName: String) = viewModelScope.launch(dispatcherProvider.io()) {
//        repository.searchMovieByName(1, movieName).fold(
//            onSuccess = { movies ->
//                print("Movies : $movies")
//                _state.update { oldState ->
//                    oldState.copy(
//                        movies = movies,
//                    )
//                }
//            },
//            onFailure = { error ->
//                Timber.e("Error: ${error.message}")
//                _state.update { oldState ->
//                    oldState.copy(
//                        errorMessage = HomeException.MovieException(error.message!!),
//                    )
//                }
//            },
//        )
//    }
}
