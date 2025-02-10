package com.apptolast.familyfilmapp.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.repositories.FirebaseAuthRepository
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

@HiltViewModel
class AppNavigationViewModel @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val roomDatasource: RoomDatasource,
) : ViewModel() {

    val userState = firebaseAuthRepository.getUser()
        .catch {
            //  TODO: Handle error and notify to the user if needed
            Timber.e(it, "Error getting user state")
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

//    fun saveUser() {
//        viewModelScope.launch {
//            userState.firstOrNull()?.let {
//                roomDatasource.insertUser(
//                    UserTable(
//                        userId = it.uid,
//                        email = it.email ?: "",
//                        language = Locale.current.language,
//                        groupIds = emptyList()
//                    ),
//                )
//            }
//        }
//    }
}
