package com.apptolast.familyfilmapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.exceptions.HomeException
import com.apptolast.familyfilmapp.repositories.BackendRepository
import com.apptolast.familyfilmapp.repositories.LocalRepository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
        repository.getMovies().fold(
            onSuccess = { movies ->
                _homeUiState.update { oldState ->
                    oldState.copy(
                        seen = movies,
                        forSeen = movies,
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
