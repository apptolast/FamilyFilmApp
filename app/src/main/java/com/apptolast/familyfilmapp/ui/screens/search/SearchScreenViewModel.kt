package com.apptolast.familyfilmapp.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.repositories.BackendRepository
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
class SearchScreenViewModel @Inject constructor(private val repository: BackendRepository) : ViewModel() {

    private val _movies = MutableStateFlow(emptyList<Movie>())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList(),
    )

    init {
//        viewModelScope.launch {
//            repository.getMovies(1).fold(
//                onSuccess = { movies ->
//                    _movies.update { movies }
//                },
//                onFailure = {
//                    // TODO
//                    Timber.e(it)
//                },
//            )
//        }
    }

//    fun getListFilmFake(): List<Movie> = repository.generateFakeFilmData(20)
}
