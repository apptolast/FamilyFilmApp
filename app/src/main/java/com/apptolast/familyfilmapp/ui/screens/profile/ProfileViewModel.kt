package com.apptolast.familyfilmapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.ui.sharedViewmodel.UsernameValidationState
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.apptolast.familyfilmapp.utils.UsernameValidator
import com.apptolast.familyfilmapp.utils.UsernameValidator.toValidationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: Repository,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    val usernameValidationState: StateFlow<UsernameValidationState>
        field: MutableStateFlow<UsernameValidationState> = MutableStateFlow(UsernameValidationState.Idle)

    val isSaving: StateFlow<Boolean>
        field: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val saveError: StateFlow<String?>
        field: MutableStateFlow<String?> = MutableStateFlow(null)

    private var usernameCheckJob: Job? = null

    fun onUsernameChange(value: String) {
        usernameCheckJob?.cancel()

        val earlyState = UsernameValidator.validate(value).toValidationState()
        if (earlyState != null) {
            usernameValidationState.update { earlyState }
            return
        }

        usernameValidationState.update { UsernameValidationState.Checking }
        usernameCheckJob = viewModelScope.launch(dispatcherProvider.io()) {
            delay(USERNAME_CHECK_DEBOUNCE_MS)
            val available = repository.isUsernameAvailable(value)
            usernameValidationState.update {
                if (available) UsernameValidationState.Available else UsernameValidationState.Taken
            }
        }
    }

    fun saveUsername(user: User, newUsername: String) = viewModelScope.launch(dispatcherProvider.io()) {
        isSaving.update { true }
        saveError.update { null }

        repository.updateUsername(user, newUsername)
            .onSuccess {
                Timber.d("Username saved: ${user.id} -> $newUsername")
                isSaving.update { false }
            }
            .onFailure { error ->
                Timber.e(error, "Error saving username")
                saveError.update { error.message ?: "Error saving username" }
                isSaving.update { false }
            }
    }

    fun resetValidationState() {
        usernameCheckJob?.cancel()
        usernameValidationState.update { UsernameValidationState.Idle }
        saveError.update { null }
    }

    companion object {
        private const val USERNAME_CHECK_DEBOUNCE_MS = 500L
    }
}
