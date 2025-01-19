package com.apptolast.familyfilmapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.repositories.BackendRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val backendRepository: BackendRepository,
    private val firebaseAuth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ProfileUiState(),
    )

//    init {
//        viewModelScope.launch {
//            backendRepository.me().fold(
//                onSuccess = { user ->
//                    _state.update { oldState ->
//                        oldState.copy(
//                            userData = user,
//                            isLogged = true,
//                        )
//                    }
//                },
//                onFailure = { error ->
//                    Timber.e(error, "Error getting user info")
//                },
//            )
//        }
//    }

    fun logOut() {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
    }
}
