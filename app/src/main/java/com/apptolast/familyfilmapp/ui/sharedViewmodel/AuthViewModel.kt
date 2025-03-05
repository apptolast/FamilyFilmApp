package com.apptolast.familyfilmapp.ui.sharedViewmodel

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
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
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
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
                        screenState.update { LoginRegisterState.Login() }
                    }
                    .onFailure { error ->
                        authState.update {
                            AuthState.Error(error.message ?: "Login Error")
                        }
                    }
            }
    }

    fun googleSignIn(context: Context) = viewModelScope.launch {

        // Handle the successfully returned credential.
        try {
            // Logic for obtaining credentials
            val credentialResponse = credentialManager.getCredential(
                request = credentialRequest,
                context = context,
            )

            val credential = credentialResponse.credential

            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    // Use googleIdTokenCredential and extract id to validate and authenticate on your server.
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                    authRepository.loginWithGoogle(googleIdTokenCredential.idToken).let {
                        it.collectLatest { result ->
                            result
                                .onSuccess { user ->
                                    if (user != null) {
                                        createNewUser(user)
                                        authState.update { AuthState.Authenticated(user) }
                                    } else {
                                        authState.update { AuthState.Unauthenticated }
                                    }
                                }
                                .onFailure { error ->
                                    Timber.e(error)
                                    authState.update { AuthState.Error(error.message.toString()) }
                                }
                        }
                    }
                } catch (e: GoogleIdTokenParsingException) {
                    Timber.e(e, "Received an invalid google id token response")
                }
            } else {
                // Catch any unrecognized custom credential type here.
                Timber.e("Unexpected type of credential")
            }

        } catch (e: GetCredentialCancellationException) {
            // Manejo de la cancelación por parte del usuario
            Timber.e(e, "Usuario canceló el inicio de sesión")
            // Aquí puedes notificar al usuario, registrar el evento, etc.
        }
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
        authState.update { AuthState.Unauthenticated }
    }

    fun deleteUser(email: String, password: String) = viewModelScope.launch(dispatcherProvider.io()) {
        val currentUser = (authState.value as? AuthState.Authenticated)?.user
        if (currentUser != null) {
            // Get user data from repository
            repository.getUserById(currentUser.uid).take(1).collectLatest { user ->
                // Delete from Firestore
                repository.deleteUser(
                    user = user,
                    success = {
                        viewModelScope.launch {
                            authRepository.deleteAccount(email, password).first().let { result ->
                                result
                                    .onSuccess {
                                        authState.update { AuthState.Unauthenticated }
                                    }
                                    .onFailure { error ->
                                        Timber.e(error)
                                        authState.update {
                                            AuthState.Error(error.message ?: "Delete User Error")
                                        }
                                    }
                            }
                        }
                    },
                    failure = { error ->
                        authState.update {
                            AuthState.Error(error.message ?: "Delete User Error")
                        }
                    },
                )
            }
        } else {
            authState.update { AuthState.Unauthenticated }
        }
    }

    fun createNewUser(user: FirebaseUser) {
        repository.createUser(
            User().copy(
                id = user.uid,
                email = user.email ?: "",
                language = Locale.getDefault().language,
            ),
            success = {},
            failure = { error ->
                Timber.e(error)
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
