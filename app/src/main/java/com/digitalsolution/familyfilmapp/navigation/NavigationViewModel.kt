package com.digitalsolution.familyfilmapp.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.CheckUserLoggedInUseCase
import com.digitalsolution.familyfilmapp.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _navigationUIState = MutableLiveData<NavigationUIState>()
    val navigationUIState: LiveData<NavigationUIState> = _navigationUIState

    init {
        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn() = viewModelScope.launch(dispatcherProvider.io()) {
        checkUserLoggedInUseCase(Unit).collectLatest { uiState ->
            _navigationUIState.postValue(
                NavigationUIState(
                    isUserLoggedIn = uiState.isLogged,
                ),
            )
        }
    }
}
