package com.digitalsolution.familyfilmapp.ui.screens.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state = mutableStateOf(false)
    val state: State<Boolean> = _state

    fun logout() {
        firebaseAuth.signOut()
        _state.value = firebaseAuth.currentUser == null
    }
}
