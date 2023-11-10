package com.digitalsolution.familyfilmapp.navigation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {

    private val _navigationUIState = MutableLiveData(NavigationUIState())
    val navigationUIState: MutableLiveData<NavigationUIState> = _navigationUIState

    init {
        viewModelScope.launch {
            delay(800)
        }
    }

    fun updateUIState(newNavigationUIState: NavigationUIState) {
        _navigationUIState.value = newNavigationUIState
    }

    fun logOut() = firebaseAuth.signOut()
}
