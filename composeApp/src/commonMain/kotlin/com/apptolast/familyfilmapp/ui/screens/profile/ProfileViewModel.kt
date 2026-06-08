package com.apptolast.familyfilmapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.analytics.AnalyticsEvents
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import com.apptolast.familyfilmapp.purchases.PurchaseFailure
import com.apptolast.familyfilmapp.purchases.PurchaseManager
import com.apptolast.familyfilmapp.rating.RateAppManager
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.ui.sharedViewmodel.UsernameValidationState
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.apptolast.familyfilmapp.utils.UsernameValidator
import com.apptolast.familyfilmapp.utils.UsernameValidator.toValidationState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: Repository,
    private val dispatcherProvider: DispatcherProvider,
    private val tmdbLocaleManager: TmdbLocaleManager,
    private val purchaseManager: PurchaseManager,
    private val rateAppManager: RateAppManager,
    private val analyticsTracker: AnalyticsTracker,
    private val crashReporter: CrashReporter,
) : ViewModel() {

    private val _usernameValidationState = MutableStateFlow<UsernameValidationState>(UsernameValidationState.Idle)
    val usernameValidationState: StateFlow<UsernameValidationState> = _usernameValidationState.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    private val _isPurchaseLoading = MutableStateFlow(false)
    val isPurchaseLoading: StateFlow<Boolean> = _isPurchaseLoading.asStateFlow()

    val hasRatedApp: StateFlow<Boolean> = rateAppManager.hasRatedApp
    val hasChatPremium: StateFlow<Boolean> = purchaseManager.hasChatPremium

    private val _purchaseEvent = MutableSharedFlow<PurchaseEvent>()
    val purchaseEvent: SharedFlow<PurchaseEvent> = _purchaseEvent.asSharedFlow()

    private var usernameCheckJob: Job? = null

    fun onUsernameChange(value: String) {
        usernameCheckJob?.cancel()
        val earlyState = UsernameValidator.validate(value).toValidationState()
        if (earlyState != null) {
            _usernameValidationState.update { earlyState }
            return
        }
        _usernameValidationState.update { UsernameValidationState.Checking }
        usernameCheckJob = viewModelScope.launch(dispatcherProvider.io()) {
            delay(USERNAME_CHECK_DEBOUNCE_MS)
            val available = repository.isUsernameAvailable(value)
            _usernameValidationState.update {
                if (available) UsernameValidationState.Available else UsernameValidationState.Taken
            }
        }
    }

    fun saveUsername(user: User, newUsername: String) = viewModelScope.launch(dispatcherProvider.io()) {
        _isSaving.update { true }
        _saveError.update { null }
        repository.updateUsername(user, newUsername)
            .onSuccess {
                analyticsTracker.logEvent(AnalyticsEvents.USERNAME_CHANGED)
                _isSaving.update { false }
            }
            .onFailure { error ->
                crashReporter.recordException(error)
                _saveError.update { error.message ?: "Error saving username" }
                _isSaving.update { false }
            }
    }

    fun resetValidationState() {
        usernameCheckJob?.cancel()
        _usernameValidationState.update { UsernameValidationState.Idle }
        _saveError.update { null }
    }

    fun markAppAsRated() {
        analyticsTracker.logEvent(AnalyticsEvents.RATE_APP_TAPPED)
        rateAppManager.markAsRated()
    }

    fun saveLanguage(user: User, languageTag: String) = viewModelScope.launch(dispatcherProvider.io()) {
        val updatedUser = user.copy(language = languageTag)
        repository.updateUser(updatedUser)
            .onSuccess { tmdbLocaleManager.update(languageTag) }
            .onFailure { error -> crashReporter.recordException(error) }
    }

    fun purchaseRemoveAds() = viewModelScope.launch(dispatcherProvider.io()) {
        if (_isPurchaseLoading.value) return@launch
        analyticsTracker.logEvent(
            AnalyticsEvents.PAYWALL_SHOWN,
            mapOf(
                AnalyticsEvents.Param.ENTRY_POINT to AnalyticsEvents.EntryPoint.PROFILE_REMOVE_ADS,
                AnalyticsEvents.Param.ENTITLEMENT to AnalyticsEvents.Entitlement.REMOVE_ADS,
            ),
        )
        _isPurchaseLoading.update { true }
        purchaseManager.purchaseRemoveAds()
            .onSuccess { _purchaseEvent.emit(PurchaseEvent.PurchaseSuccess) }
            .onFailure { error ->
                if (error !is PurchaseFailure.Cancelled) {
                    crashReporter.recordException(error)
                    _purchaseEvent.emit(PurchaseEvent.PurchaseError(error.message))
                }
            }
        _isPurchaseLoading.update { false }
    }

    fun purchaseChatPremium() = viewModelScope.launch(dispatcherProvider.io()) {
        if (_isPurchaseLoading.value) return@launch
        analyticsTracker.logEvent(
            AnalyticsEvents.PAYWALL_SHOWN,
            mapOf(
                AnalyticsEvents.Param.ENTRY_POINT to AnalyticsEvents.EntryPoint.PROFILE_CHAT_PREMIUM,
                AnalyticsEvents.Param.ENTITLEMENT to AnalyticsEvents.Entitlement.CHAT_PREMIUM,
            ),
        )
        _isPurchaseLoading.update { true }
        purchaseManager.purchaseChatPremium()
            .onSuccess { _purchaseEvent.emit(PurchaseEvent.PurchaseSuccess) }
            .onFailure { error ->
                if (error !is PurchaseFailure.Cancelled) {
                    crashReporter.recordException(error)
                    _purchaseEvent.emit(PurchaseEvent.PurchaseError(error.message))
                }
            }
        _isPurchaseLoading.update { false }
    }

    fun restorePurchases() = viewModelScope.launch(dispatcherProvider.io()) {
        if (_isPurchaseLoading.value) return@launch
        _isPurchaseLoading.update { true }
        purchaseManager.restorePurchases()
            .onSuccess { restored ->
                if (restored) {
                    _purchaseEvent.emit(PurchaseEvent.RestoreSuccess)
                } else {
                    _purchaseEvent.emit(PurchaseEvent.RestoreNothingFound)
                }
            }
            .onFailure { error ->
                crashReporter.recordException(error)
                _purchaseEvent.emit(PurchaseEvent.RestoreError(error.message))
            }
        _isPurchaseLoading.update { false }
    }

    private companion object {
        const val USERNAME_CHECK_DEBOUNCE_MS = 500L
    }
}

sealed interface PurchaseEvent {
    data object PurchaseSuccess : PurchaseEvent
    data class PurchaseError(val message: String?) : PurchaseEvent
    data object RestoreSuccess : PurchaseEvent
    data object RestoreNothingFound : PurchaseEvent
    data class RestoreError(val message: String?) : PurchaseEvent
}
