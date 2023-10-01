package com.digitalsolution.familyfilmapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.repositories.FilmRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val filmRepository: FilmRepository
) : ViewModel() {


    init {
        viewModelScope.launch {
            delay(800)
        }
    }

    fun logOut() = firebaseAuth.signOut()

    fun getGroupsList() = filmRepository.generateGroups(12)
}
