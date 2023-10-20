package com.digitalsolution.familyfilmapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.model.local.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ProfileUiState(),
    )

    init {
        viewModelScope.launch {
            _state.update { st ->
                st.copy(
                    userData = User(
                        email = firebaseAuth.currentUser?.email.toString(),
                        name = firebaseAuth.currentUser?.displayName.toString(),
                        pass = "",
                        photo = firebaseAuth.currentUser?.photoUrl.toString(),
                    ),
                )
            }
        }
    }

    fun logOut() {
        firebaseAuth.signOut()
    }
}
