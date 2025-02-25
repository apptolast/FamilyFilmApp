package com.apptolast.familyfilmapp.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.model.room.UserTable
import com.apptolast.familyfilmapp.repositories.FirebaseAuthRepository
import com.apptolast.familyfilmapp.repositories.datasources.RoomDatasource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AppNavigationViewModel @Inject constructor(
    authRepository: FirebaseAuthRepository,
    private val roomDatasource: RoomDatasource,
) : ViewModel() {

    val userState = authRepository.getUser()
        .catch {
            Timber.e(it, "Error getting user state")
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    init {
        viewModelScope.launch {
            userState.filterNotNull().collect { user ->
                if (user.isEmailVerified)
                    roomDatasource.insertUser(
                        UserTable(user.uid).copy(
                            email = user.email ?: "",
                            language = Locale.getDefault().language,
                        ),
                    )
            }
        }
    }
}
