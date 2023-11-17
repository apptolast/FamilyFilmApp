package com.digitalsolution.familyfilmapp.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.CheckUserLoggedInUseCase
import com.digitalsolution.familyfilmapp.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _navigationUIState = MutableStateFlow(NavigationUIState())
    val navigationUIState: StateFlow<NavigationUIState> = _navigationUIState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = NavigationUIState(),
    )

    init {
        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn() = viewModelScope.launch(dispatcherProvider.io()) {
        checkUserLoggedInUseCase(Unit).collectLatest { uiState ->
            _navigationUIState.update { oldState ->
                oldState.copy(
                    isUserLoggedIn = uiState.isLogged,
                )
            }
        }
    }
}
