package com.apptolast.familyfilmapp.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.repositories.FirebaseRepository
import com.apptolast.familyfilmapp.repositories.LocalRepository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class AppNavigationViewModel @Inject constructor(
    firebaseRepository: FirebaseRepository,
    localRepository: LocalRepository,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    val userState = firebaseRepository.getUser()
        .catch {
            //  TODO: Handle error and notify to the user if needed
            Timber.e(it, "Error getting user state")
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        ).also {
            viewModelScope.launch {
                it.collectLatest { user ->
                    user?.getIdToken(false)?.addOnSuccessListener {
                        localRepository.setToken(it.token ?: "")
                    }
                }
            }
        }
//        .also {
//            viewModelScope.launch {
//                it.value?.let { user ->
//                    user.getIdToken(false).addOnSuccessListener {
//                        localRepository.setToken(it.token ?: "")
//                    }
//                }
//            }
//        }
}
