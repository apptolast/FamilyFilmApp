package com.digitalsolution.familyfilmapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.exceptions.HomeException
import com.digitalsolution.familyfilmapp.repositories.BackendRepository
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.CheckUserLoggedInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase,
    private val repository: BackendRepository,
) : ViewModel() {

    private val _isUserLoggedIn = MutableStateFlow(true)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = true,
    )

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HomeUiState(),
    )

    init {
        viewModelScope.launch {
            awaitAll(
                async { checkUserLoggedIn() },
                async { getGroups() },
                async { getMovies() },
            )
        }
    }

    private suspend fun getMovies() {
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

    private suspend fun getGroups() {
        repository.getGroups().fold(
            onSuccess = { groups ->
                _homeUiState.update { oldState ->
                    oldState.copy(
                        groups = groups.map { it.name },
                    )
                }
            },
            onFailure = {
                _homeUiState.update { oldState ->
                    oldState.copy(
                        errorMessage = HomeException.GroupsException(),
                    )
                }
            },
        )
    }

    private suspend fun checkUserLoggedIn() {
        checkUserLoggedInUseCase(Unit).collectLatest { loginState ->
            _isUserLoggedIn.update {
                loginState.isLogged
            }
        }
    }
}
