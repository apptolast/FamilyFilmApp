package com.apptolast.familyfilmapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.apptolast.familyfilmapp.repositories.BackendRepository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BackendRepository,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

//    private val _movies: MutableStateFlow<PagingData<Movie>> = MutableStateFlow(value = PagingData.empty())
//    val movies = _movies.asStateFlow()

    val movies = repository.getMovies()
        .catch { error ->
            Timber.e(error, "Error getting movies")
        }
        .distinctUntilChanged()
        .cachedIn(viewModelScope)


//    init {
//        viewModelScope.launch {
//            repository.getMovies()
//                .catch{ error ->
//                    Timber.e(error, "Error getting movies")
//                }
//                .distinctUntilChanged()
//                .cachedIn(viewModelScope)
//                .collect {
//                    _movies.value = it
//                }
//        }
//    }

    fun searchMovieByName(filter: String) = viewModelScope.launch(dispatcherProvider.io()) {
        if (filter.isEmpty()) {
            _state.update { it.copy(filterMovies = emptyList()) }
        } else {
            repository.searchMovieByName(filter).getOrNull()?.let { movies ->
                _state.update { it.copy(filterMovies = movies) }
            }
        }
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
