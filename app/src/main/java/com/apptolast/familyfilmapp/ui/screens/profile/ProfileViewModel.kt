package com.apptolast.familyfilmapp.ui.screens.profile

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import com.apptolast.familyfilmapp.purchases.PurchaseManager
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.ui.sharedViewmodel.UsernameValidationState
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.apptolast.familyfilmapp.utils.UsernameValidator
import com.apptolast.familyfilmapp.utils.UsernameValidator.toValidationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: Repository,
    private val dispatcherProvider: DispatcherProvider,
    private val tmdbLocaleManager: TmdbLocaleManager,
    private val purchaseManager: PurchaseManager,
) : ViewModel() {

    val usernameValidationState: StateFlow<UsernameValidationState>
        field: MutableStateFlow<UsernameValidationState> = MutableStateFlow(UsernameValidationState.Idle)

    val isSaving: StateFlow<Boolean>
        field: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val saveError: StateFlow<String?>
        field: MutableStateFlow<String?> = MutableStateFlow(null)

    val isPurchaseLoading: StateFlow<Boolean>
        field: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _purchaseEvent = MutableSharedFlow<PurchaseEvent>()
    val purchaseEvent: SharedFlow<PurchaseEvent> = _purchaseEvent.asSharedFlow()

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

    fun saveLanguage(user: User, languageTag: String) = viewModelScope.launch(dispatcherProvider.io()) {
        val updatedUser = user.copy(language = languageTag)
        repository.updateUser(updatedUser)
            .onSuccess {
                tmdbLocaleManager.update(languageTag)
                Timber.d("Language updated to: $languageTag")
            }
            .onFailure { error ->
                Timber.e(error, "Error saving language")
            }
    }

    fun purchaseRemoveAds(activity: Activity) = viewModelScope.launch(dispatcherProvider.io()) {
        if (isPurchaseLoading.value) return@launch
        isPurchaseLoading.update { true }
        purchaseManager.purchaseRemoveAds(
            activity = activity,
            onPurchaseStart = { isPurchaseLoading.update { false } },
        )
            .onSuccess {
                Timber.d("Remove ads purchase successful")
                _purchaseEvent.emit(PurchaseEvent.PurchaseSuccess)
            }
            .onFailure { error ->
                Timber.e(error, "Remove ads purchase failed")
                val isCancelled = error.message?.contains("cancel", ignoreCase = true) == true
                if (!isCancelled) {
                    _purchaseEvent.emit(PurchaseEvent.PurchaseError(error.message))
                }
            }
        isPurchaseLoading.update { false }
    }

    fun restorePurchases() = viewModelScope.launch(dispatcherProvider.io()) {
        if (isPurchaseLoading.value) return@launch
        isPurchaseLoading.update { true }
        purchaseManager.restorePurchases()
            .onSuccess { restored ->
                Timber.d("Restore purchases result: adsRemoved=$restored")
                if (restored) {
                    _purchaseEvent.emit(PurchaseEvent.RestoreSuccess)
                } else {
                    _purchaseEvent.emit(PurchaseEvent.RestoreNothingFound)
                }
            }
            .onFailure { error ->
                Timber.e(error, "Restore purchases failed")
                _purchaseEvent.emit(PurchaseEvent.RestoreError(error.message))
            }
        isPurchaseLoading.update { false }
    }

    companion object {
        private const val USERNAME_CHECK_DEBOUNCE_MS = 500L
    }
}

sealed interface PurchaseEvent {
    data object PurchaseSuccess : PurchaseEvent
    data class PurchaseError(val message: String?) : PurchaseEvent
    data object RestoreSuccess : PurchaseEvent
    data object RestoreNothingFound : PurchaseEvent
    data class RestoreError(val message: String?) : PurchaseEvent
}
