package com.apptolast.familyfilmapp.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DetailScreenViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DetailScreenUIState())
    val uiState: StateFlow<DetailScreenUIState> = _uiState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = DetailScreenUIState(),
    )

    fun addMovieToWatchList(groupId: Int, movieId: Int) = viewModelScope.launch {
//        watchListUseCase(groupId to movieId).collectLatest { newState ->
//            _uiState.update {
//                newState
//            }
//        }
    }

    fun addMovieToSeenList(groupId: Int, movieId: Int) = viewModelScope.launch {
//        seenListUseCase(groupId to movieId).collectLatest { newState ->
//            _uiState.update {
//                newState
//            }
//        }
    }
}
