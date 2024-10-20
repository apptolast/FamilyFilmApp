package com.apptolast.familyfilmapp.navigation

import androidx.lifecycle.ViewModel
import com.apptolast.familyfilmapp.repositories.BackendRepository
import com.apptolast.familyfilmapp.repositories.FirebaseRepository
import com.apptolast.familyfilmapp.repositories.LocalRepository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppNavigationViewModel @Inject constructor(
    firebaseRepository: FirebaseRepository,
    repository: BackendRepository,
    localRepository: LocalRepository,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

//    val userState = firebaseRepository.getUser()
//        .catch {
//            //  TODO: Handle error and notify to the user if needed
//            Timber.e(it, "Error getting user state")
//        }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.Eagerly,
//            initialValue = null,
//        ).also {
//            viewModelScope.launch {
//                it.collectLatest { user ->
//                    user?.getIdToken(false)?.addOnSuccessListener {
//                        localRepository.setToken(it.token ?: "")
//                    }
//                }
//            }
//        }
}
