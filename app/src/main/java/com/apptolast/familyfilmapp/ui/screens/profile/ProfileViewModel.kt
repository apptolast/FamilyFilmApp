package com.apptolast.familyfilmapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.repositories.Repository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: Repository,
    private val firebaseAuth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient,
) : ViewModel() {

    val state: StateFlow<ProfileUiState>
        field: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState())

    init {
        viewModelScope.launch {
            repository.getUserById(firebaseAuth.currentUser!!.uid).first().let { user ->
                state.update {
                    it.copy(
                        userData = user,
                        isLogged = true,
                    )
                }
            }
        }
    }

    fun logOut() {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
    }
}
