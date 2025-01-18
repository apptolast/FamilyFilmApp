package com.apptolast.familyfilmapp.ui.screens.search

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.repositories.BackendRepository
import com.apptolast.familyfilmapp.ui.screens.recommend.states.MovieUiState
import com.apptolast.familyfilmapp.ui.screens.search.states.SearchScreenUI
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: BackendRepository) : ViewModel() {

    private val _state = MutableStateFlow(MovieUiState())
    val state: StateFlow<MovieUiState> = _state.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MovieUiState(),
    )

    private val _uiState = MutableStateFlow(SearchScreenUI())
    val uiState: StateFlow<SearchScreenUI> = _uiState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = SearchScreenUI(),
    )

    init {
//        viewModelScope.launch {
//            _state.update { oldState ->
//                oldState.copy(
//                    movies = repository.getMovies(1).getOrElse {
//                        Timber.e(it)
//                        emptyList()
//                    },
//                )
//            }
//        }
    }

    fun onSearchQueryChanged(query: String) = viewModelScope.launch {
        _uiState.update {
            it.copy(
                searchQuery = mutableStateOf(query),
            )
        }
    }

    fun getMovieQuery() = viewModelScope.launch {
        val filteredMovies = _state.value.movies.filter {
            it.title.contains(_uiState.value.searchQuery.value, ignoreCase = true)
        }

        _uiState.update {
            it.copy(
                searchResults = mutableStateOf(filteredMovies),
            )
        }
    }
}
