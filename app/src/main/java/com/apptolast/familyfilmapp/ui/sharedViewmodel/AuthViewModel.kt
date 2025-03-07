package com.apptolast.familyfilmapp.ui.sharedViewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.FirebaseAuthRepository
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassState
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val repository: Repository,
    private val dispatcherProvider: DispatcherProvider,
    private val credentialManager: CredentialManager,
    private val credentialRequest: GetCredentialRequest,
) : ViewModel() {

    val authState: StateFlow<AuthState>
        field: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Unauthenticated)

    val screenState: StateFlow<LoginRegisterState>
        field: MutableStateFlow<LoginRegisterState> = MutableStateFlow(LoginRegisterState.Login())

    val email: StateFlow<String>
        field: MutableStateFlow<String> = MutableStateFlow("")

    val password: StateFlow<String>
        field: MutableStateFlow<String> = MutableStateFlow("")

    val recoverPassState: StateFlow<RecoverPassState>
        field: MutableStateFlow<RecoverPassState> = MutableStateFlow(RecoverPassState())

    val provider: StateFlow<String?> = authRepository.getProvider().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    init {
        viewModelScope.launch {
            authRepository.getUser().combine(authRepository.isTokenValid()) { user, isTokenValid ->
                user to isTokenValid
            }.catch { error ->
                Timber.e(error, "Error getting user")
                authState.update { AuthState.Error(error.message ?: "Error getting the user") }
            }.collectLatest { (user, isTokenValid) ->
                if (user?.isEmailVerified == true && isTokenValid) {
                    AuthState.Authenticated(user)
                } else {
                    AuthState.Unauthenticated
                }.let { newState ->
                    authState.update { newState }
                }
            }
        }
    }

    fun changeScreenState() {
        screenState.update {
            when (screenState.value) {
                is LoginRegisterState.Login -> LoginRegisterState.Register()
                is LoginRegisterState.Register -> LoginRegisterState.Login()
            }
        }

        authState.update { AuthState.Unauthenticated }
    }

    fun login(email: String, password: String) = viewModelScope.launch(dispatcherProvider.io()) {
        authState.update { AuthState.Loading }

        authRepository.login(email, password)
            .catch { error ->
                authState.update {
                    AuthState.Error(error.message ?: "Login Error")
                }
            }.collect { result ->
                result
                    .onSuccess { user ->
                        authState.update {
                            AuthState.Authenticated(user!!)
                        }
                    }
                    .onFailure { error ->
                        authState.update {
                            AuthState.Error(error.message ?: "Login Error")
                        }
                    }
            }
    }

    fun register(email: String, password: String) = viewModelScope.launch(dispatcherProvider.io()) {
        authState.update { AuthState.Loading }
        authRepository.register(email, password)
            .catch {
                Timber.e(it)
            }
            .filterNotNull()
            .collectLatest { result ->
                result
                    .onSuccess { user ->
                        if (user == null) return@collectLatest
                        createNewUser(user)
                        authState.update { AuthState.Authenticated(user) }
                    }
                    .onFailure { error ->
                        authState.update {
                            AuthState.Error(error.message ?: "Login Error")
                        }
                    }
            }
    }

    fun googleSignIn(context: Context) = viewModelScope.launch {
        try {
            val result = credentialManager.getCredential(
                request = credentialRequest,
                context = context,
            )
            handleSignIn(result)
        } catch (e: GetCredentialException) {
            handleFailure(e)
        } catch (e: Exception) {
            handleFailure(e)
        }
    }

    fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        val credential = result.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                        viewModelScope.launch {
                            authRepository.loginWithGoogle(googleIdTokenCredential.idToken).first().let { result ->
                                result
                                    .onSuccess { user ->
                                        if (user != null) {
                                            createNewUser(user)
                                            authState.update { AuthState.Authenticated(user) }
                                        } else {
                                            handleFailure(null, "User not found")
                                        }
                                    }
                                    .onFailure { error ->
                                        handleFailure(error)
                                    }
                            }
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        handleFailure(e, "Received an invalid google id token response")
                    }
                } else {
                    // Catch any unrecognized credential type here.
                    handleFailure(null, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                handleFailure(null, "Unexpected type of credential")
            }
        }
    }

    @SuppressLint("TimberExceptionLogging")
    private fun handleFailure(e: Throwable? = null, message: String? = null) {
        Timber.e(e, message)
        authState.update { AuthState.Error(e?.message ?: message ?: "") }
        authState.update { AuthState.Unauthenticated }
    }

    fun recoverPassword(email: String) = viewModelScope.launch(dispatcherProvider.io()) {
        authRepository.recoverPassword(email)
            .catch { error ->
                recoverPassState.update {
                    it.copy(
                        errorMessage = error.message ?: "Recover Password Error",
                        isLoading = false,
                    )
                }
            }
            .collectLatest { result ->
                result
                    .onSuccess {
                        recoverPassState.update {
                            it.copy(
                                isLoading = false,
                                isSuccessful = true,
                                errorMessage = null,
                            )
                        }
                    }
                    .onFailure { error ->
                        recoverPassState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Recover Password Error",
                            )
                        }
                    }
            }
    }

    fun updateRecoveryPasswordState(newRecoverPassState: RecoverPassState) {
        recoverPassState.update { newRecoverPassState }
    }

    fun logOut() {
        authRepository.logOut()
        clearGoogleCredentials()
        authState.update { AuthState.Unauthenticated }
    }

    fun deleteUser(email: String = "", password: String = "") = viewModelScope.launch(dispatcherProvider.io()) {
        val currentUser = (authState.value as? AuthState.Authenticated)?.user
        if (currentUser != null) {
            // Get user data from repository
            repository.getUserById(currentUser.uid).take(1).collectLatest { user ->
                // Delete from Firestore
                repository.deleteUser(
                    user = user,
                    success = {
                        viewModelScope.launch {
                            when (provider.value) {
                                GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD -> {
                                    authRepository.deleteGoogleAccount().first().let { result ->
                                        result
                                            .onSuccess {
                                                clearGoogleCredentials()
                                                authState.update { AuthState.Unauthenticated }
                                            }
                                            .onFailure { error ->
                                                handleFailure(error, "Delete User Error")
                                            }
                                    }
                                }

                                EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD -> {
                                    authRepository.deleteAccountWithReAuthentication(email, password).first()
                                        .let { result ->
                                            result
                                                .onSuccess {
                                                    authState.update { AuthState.Unauthenticated }
                                                }
                                                .onFailure { error ->
                                                    handleFailure(error, "Delete User Error")
                                                }
                                        }
                                }

                                else -> {
                                    Timber.w(IllegalStateException("Credential not expected"))
                                }
                            }
                        }
                    },
                    failure = { error ->
                        handleFailure(error, "Delete User Error")
                    },
                )
            }
        } else {
            handleFailure(null, "Delete user - No user found")
        }
    }

    private fun clearGoogleCredentials() = viewModelScope.launch {
        if (provider.value == GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD) {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        }
    }

    fun createNewUser(user: FirebaseUser) {
        repository.createUser(
            User().copy(
                id = user.uid,
                email = user.email ?: "",
                language = Locale.getDefault().toLanguageTag(),
            ),
            success = {},
            failure = { error ->
                handleFailure(error)
            },
        )
    }
}

sealed interface AuthState {
    object Loading : AuthState
    object Unauthenticated : AuthState
    data class Authenticated(val user: FirebaseUser) : AuthState
    data class Error(val message: String, val id: UUID = UUID.randomUUID()) : AuthState
}
