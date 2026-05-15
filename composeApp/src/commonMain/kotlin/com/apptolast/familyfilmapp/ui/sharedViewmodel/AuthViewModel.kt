@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)

package com.apptolast.familyfilmapp.ui.sharedViewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.analytics.AnalyticsEvents
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.analytics.UserProperties
import com.apptolast.familyfilmapp.auth.GoogleSignInClient
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.firebase.toDomainUserModel
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.network.systemLanguageTag
import com.apptolast.familyfilmapp.purchases.PurchaseManager
import com.apptolast.familyfilmapp.repositories.FirebaseAuthRepository
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassState
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.apptolast.familyfilmapp.utils.UsernameValidator
import com.apptolast.familyfilmapp.utils.UsernameValidator.toValidationState
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidUserException
import dev.gitlive.firebase.auth.FirebaseAuthUserCollisionException
import dev.gitlive.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AuthViewModel(
    private val authRepository: FirebaseAuthRepository,
    private val repository: Repository,
    private val dispatcherProvider: DispatcherProvider,
    private val googleSignInClient: GoogleSignInClient,
    private val purchaseManager: PurchaseManager,
    private val analyticsTracker: AnalyticsTracker,
    private val crashReporter: CrashReporter,
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    private val _screenState = MutableStateFlow<LoginRegisterState>(LoginRegisterState.Login())
    val screenState: StateFlow<LoginRegisterState> = _screenState

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isEmailSent = MutableStateFlow(false)
    val isEmailSent: StateFlow<Boolean> = _isEmailSent

    private val _recoverPassState = MutableStateFlow(RecoverPassState())
    val recoverPassState: StateFlow<RecoverPassState> = _recoverPassState

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _usernameValidationState = MutableStateFlow<UsernameValidationState>(UsernameValidationState.Idle)
    val usernameValidationState: StateFlow<UsernameValidationState> = _usernameValidationState

    private val _shouldPromptForUsername = MutableStateFlow(false)
    val shouldPromptForUsername: StateFlow<Boolean> = _shouldPromptForUsername

    val provider: StateFlow<String?> = authRepository.getProvider().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    private var usernameCheckJob: Job? = null

    init {
        viewModelScope.launch {
            awaitAll(
                async { checkIsUserLogged() },
                async { verifyEmail() },
            )
        }

        viewModelScope.launch(dispatcherProvider.io()) {
            purchaseManager.hasRemovedAds.collectLatest { adsRemoved ->
                val currentUser = (authState.value as? AuthState.Authenticated)?.user
                    ?: return@collectLatest
                if (currentUser.hasRemovedAds != adsRemoved) {
                    repository.updateHasRemovedAds(currentUser.id, adsRemoved)
                }
            }
        }

        viewModelScope.launch {
            authState.collectLatest { state ->
                val user = (state as? AuthState.Authenticated)?.user
                analyticsTracker.setUserId(user?.id)
                analyticsTracker.setUserProperty(
                    UserProperties.IS_EMAIL_VERIFIED,
                    user?.let { (it.email.isNotBlank()).toString() },
                )
            }
        }
        viewModelScope.launch {
            purchaseManager.hasRemovedAds.collectLatest { value ->
                analyticsTracker.setUserProperty(UserProperties.HAS_REMOVED_ADS, value.toString())
            }
        }
        viewModelScope.launch {
            purchaseManager.hasChatPremium.collectLatest { value ->
                analyticsTracker.setUserProperty(UserProperties.HAS_CHAT_PREMIUM, value.toString())
            }
        }
    }

    fun changeScreenState() {
        _screenState.update {
            when (it) {
                is LoginRegisterState.Login -> LoginRegisterState.Register()
                is LoginRegisterState.Register -> LoginRegisterState.Login()
            }
        }
        _username.update { "" }
        usernameCheckJob?.cancel()
        _usernameValidationState.update { UsernameValidationState.Idle }
        _authState.update { AuthState.Unauthenticated }
    }

    fun onUsernameChange(value: String) {
        _username.update { value }
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

    private fun checkIsUserLogged() = viewModelScope.launch(dispatcherProvider.io()) {
        authRepository.getUser().combine(authRepository.isTokenValid()) { user, isTokenValid ->
            user to isTokenValid
        }.catch { error ->
            crashReporter.recordException(error)
            handleFailure(error.message ?: "Error getting the user")
        }.flatMapLatest { (user, isTokenValid) ->
            if (user?.isEmailVerified == true && isTokenValid) {
                val domainUser = user.toDomainUserModel()
                repository.startSync(domainUser.id)
                purchaseManager.initialize(domainUser.id)
                repository.getUserById(domainUser.id).map { roomUser ->
                    purchaseManager.setAdsRemoved(roomUser.hasRemovedAds)
                    AuthState.Authenticated(roomUser)
                }
            } else {
                repository.stopSync()
                flowOf(AuthState.Unauthenticated as AuthState)
            }
        }.collectLatest { state ->
            _authState.update { state }
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch(dispatcherProvider.io()) {
        _authState.update { AuthState.Loading }
        authRepository.login(email, password)
            .catch { error -> handleFailure(error.message ?: "Error Sign in user") }
            .collect { result ->
                result
                    .onSuccess { user ->
                        if (user != null) {
                            repository.startSync(user.id)
                            _authState.update { AuthState.Authenticated(user) }
                            analyticsTracker.logLogin(AnalyticsEvents.Method.EMAIL)
                        } else {
                            handleFailure("Login successful but user data is null")
                        }
                    }
                    .onFailure { error ->
                        analyticsTracker.logEvent(
                            AnalyticsEvents.LOGIN_FAILED,
                            mapOf(
                                AnalyticsEvents.Param.METHOD to AnalyticsEvents.Method.EMAIL,
                                AnalyticsEvents.Param.ERROR_TYPE to error.toAuthErrorType(),
                            ),
                        )
                        handleFailure(error.message ?: "Error login")
                    }
            }
    }

    fun registerAndSendEmail(email: String, password: String, username: String) =
        viewModelScope.launch(dispatcherProvider.io()) {
            _authState.update { AuthState.Loading }
            authRepository.register(email, password)
                .catch { error ->
                    handleFailure(error.message ?: "Error register user")
                    crashReporter.recordException(error)
                }
                .filterNotNull()
                .collectLatest { result ->
                    result
                        .onSuccess { user ->
                            if (user == null) return@collectLatest
                            createNewUser(user, username)
                            _isEmailSent.update { true }
                            analyticsTracker.logSignUp(AnalyticsEvents.Method.EMAIL)
                        }
                        .onFailure { error ->
                            analyticsTracker.logEvent(
                                AnalyticsEvents.SIGN_UP_FAILED,
                                mapOf(
                                    AnalyticsEvents.Param.METHOD to AnalyticsEvents.Method.EMAIL,
                                    AnalyticsEvents.Param.ERROR_TYPE to error.toAuthErrorType(),
                                ),
                            )
                            handleFailure(error.message ?: "Error register user")
                        }
                }
        }

    private suspend fun verifyEmail() {
        authRepository.checkEmailVerification()
            .catch { error ->
                crashReporter.recordException(error)
                handleFailure(error.message ?: "Error verification user")
            }
            .collectLatest { isVerified ->
                if (isVerified) {
                    _isEmailSent.update { false }
                    analyticsTracker.logEvent(AnalyticsEvents.EMAIL_VERIFIED)
                    checkIsUserLogged()
                }
            }
    }

    fun googleSignIn() = viewModelScope.launch {
        try {
            val tokens = googleSignInClient.signIn() ?: run {
                handleFailure("Google sign-in cancelled or no credential available")
                return@launch
            }
            authRepository.loginWithGoogle(tokens).first()
                .onSuccess { user ->
                    val exists = repository.checkIfUserExists(user.id)
                    if (!exists) {
                        createNewUser(user)
                        _shouldPromptForUsername.update { true }
                        analyticsTracker.logSignUp(AnalyticsEvents.Method.GOOGLE)
                    } else {
                        analyticsTracker.logLogin(AnalyticsEvents.Method.GOOGLE)
                    }
                    checkIsUserLogged()
                }
                .onFailure { error ->
                    analyticsTracker.logEvent(
                        AnalyticsEvents.LOGIN_FAILED,
                        mapOf(
                            AnalyticsEvents.Param.METHOD to AnalyticsEvents.Method.GOOGLE,
                            AnalyticsEvents.Param.ERROR_TYPE to error.toAuthErrorType(),
                        ),
                    )
                    handleFailure(error.message ?: "Google Login Error")
                }
        } catch (e: Throwable) {
            crashReporter.recordException(e)
            handleFailure(e.message)
        }
    }

    private fun handleFailure(message: String? = null, e: Throwable? = null) {
        _authState.update { AuthState.Error(e?.message ?: message ?: "Error") }
        if (e != null) crashReporter.recordException(e)
    }

    fun clearFailure() {
        _authState.update { AuthState.Unauthenticated }
    }

    fun recoverPassword(email: String) = viewModelScope.launch(dispatcherProvider.io()) {
        authRepository.recoverPassword(email)
            .catch { error ->
                _recoverPassState.update {
                    it.copy(errorMessage = error.message ?: "Recover Password Error", isLoading = false)
                }
            }
            .collectLatest { result ->
                result
                    .onSuccess {
                        analyticsTracker.logEvent(AnalyticsEvents.PASSWORD_RECOVERY_SENT)
                        _recoverPassState.update {
                            it.copy(isLoading = false, isSuccessful = true, errorMessage = null)
                        }
                    }
                    .onFailure { error ->
                        _recoverPassState.update {
                            it.copy(isLoading = false, errorMessage = error.message ?: "Recover Password Error")
                        }
                    }
            }
    }

    fun updateRecoveryPasswordState(newRecoverPassState: RecoverPassState) {
        _recoverPassState.update { newRecoverPassState }
    }

    fun logOut() {
        analyticsTracker.logEvent(AnalyticsEvents.LOGOUT)
        repository.stopSync()
        authRepository.logOut()
        purchaseManager.logout()
        viewModelScope.launch {
            googleSignInClient.signOut()
        }
        viewModelScope.launch(dispatcherProvider.io()) {
            repository.clearLocalData()
        }
        _authState.update { AuthState.Unauthenticated }
    }

    fun deleteUser(email: String = "", password: String = "") = viewModelScope.launch(dispatcherProvider.io()) {
        val currentUser = (authState.value as? AuthState.Authenticated)?.user
        if (currentUser == null) {
            handleFailure("Delete user - No user found")
            return@launch
        }
        repository.getUserById(currentUser.id).take(1).collectLatest { user ->
            repository.deleteUser(user)
                .onSuccess { deleteAuthAccount(email, password) }
                .onFailure { handleFailure("Delete User Error") }
        }
    }

    private suspend fun deleteAuthAccount(email: String, password: String) {
        when (provider.value) {
            GOOGLE_PROVIDER_ID -> {
                authRepository.deleteGoogleAccount().first()
                    .onSuccess {
                        analyticsTracker.logEvent(
                            AnalyticsEvents.ACCOUNT_DELETED,
                            mapOf(AnalyticsEvents.Param.METHOD to AnalyticsEvents.Method.GOOGLE),
                        )
                        purchaseManager.logout()
                        googleSignInClient.signOut()
                        _authState.update { AuthState.Unauthenticated }
                    }
                    .onFailure { handleFailure("Delete User Error") }
            }
            PASSWORD_PROVIDER_ID -> {
                authRepository.deleteAccountWithReAuthentication(email, password).first()
                    .onSuccess {
                        analyticsTracker.logEvent(
                            AnalyticsEvents.ACCOUNT_DELETED,
                            mapOf(AnalyticsEvents.Param.METHOD to AnalyticsEvents.Method.EMAIL),
                        )
                        purchaseManager.logout()
                        _authState.update { AuthState.Unauthenticated }
                    }
                    .onFailure { handleFailure("Delete User Error") }
            }
            else -> {
                crashReporter.recordException(IllegalStateException("Unexpected auth provider: ${provider.value}"))
            }
        }
    }

    // Never surface raw exception messages — they can leak user-controlled input.
    private fun Throwable.toAuthErrorType(): String = when (this) {
        is FirebaseAuthInvalidCredentialsException -> AnalyticsEvents.ErrorType.INVALID_CREDENTIALS
        is FirebaseAuthInvalidUserException -> AnalyticsEvents.ErrorType.USER_DISABLED
        is FirebaseAuthUserCollisionException -> AnalyticsEvents.ErrorType.ALREADY_EXISTS
        is FirebaseAuthWeakPasswordException -> AnalyticsEvents.ErrorType.WEAK_PASSWORD
        else -> AnalyticsEvents.ErrorType.OTHER
    }

    private fun createNewUser(user: User, username: String = "") = viewModelScope.launch(dispatcherProvider.io()) {
        val newUser = User(
            id = user.id,
            email = user.email,
            language = systemLanguageTag(),
            photoUrl = user.photoUrl,
        )
        repository.createUser(newUser).onFailure { error ->
            handleFailure(error.message ?: "Create New User Error")
            return@launch
        }
        val usernameToSet = username.takeIf { it.isNotBlank() }
        if (usernameToSet != null) {
            repository.updateUsername(newUser, usernameToSet).onFailure { error ->
                crashReporter.recordException(error)
            }
        }
    }

    fun saveUsernameForNewUser(newUsername: String) = viewModelScope.launch(dispatcherProvider.io()) {
        val currentUser = (authState.value as? AuthState.Authenticated)?.user ?: return@launch
        repository.updateUsername(currentUser, newUsername)
            .onSuccess { _shouldPromptForUsername.update { false } }
            .onFailure { error -> handleFailure(error.message ?: "Failed to save username") }
    }

    fun skipUsernameSetup() {
        _shouldPromptForUsername.update { false }
    }

    private companion object {
        const val USERNAME_CHECK_DEBOUNCE_MS = 500L

        // GitLive returns provider ID strings (not enum) from FirebaseUser.providerData.
        const val GOOGLE_PROVIDER_ID = "google.com"
        const val PASSWORD_PROVIDER_ID = "password"
    }
}

sealed interface AuthState {
    data object Loading : AuthState
    data object Unauthenticated : AuthState
    data class Authenticated(val user: User) : AuthState
    data class Error(val message: String?, val id: String = Uuid.random().toString()) : AuthState
}
