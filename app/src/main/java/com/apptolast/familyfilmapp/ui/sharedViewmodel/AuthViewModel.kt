package com.apptolast.familyfilmapp.ui.sharedViewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.BuildConfig
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.FirebaseAuthRepository
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassState
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
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

    var credential: Credential? = null //

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
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(BuildConfig.WEB_ID_CLIENT)
            .setAutoSelectEnabled(true)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )
            handleSignIn(result)
        } catch (e: GetCredentialException) {
            handleFailure(e)
        } catch (e: Exception) {
            handleFailure(e)
        }
        /*
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
                                                authState.update { AuthState.Authenticated(user) }
                                            } else {
                                                authState.update { AuthState.Error("User not found") }
                                                authState.update { AuthState.Unauthenticated }
                                            }
                                        }
                                        .onFailure { error ->
                                            Timber.e(error)
                                            authState.update { AuthState.Error(error.message.toString()) }
                                            authState.update { AuthState.Unauthenticated }
                                        }
                                }
                            }
                        } catch (e: GoogleIdTokenParsingException) {
                            authState.update { AuthState.Error(e.message.toString()) }
                            authState.update { AuthState.Unauthenticated }
                            Timber.e(e, "Received an invalid google id token response")
                        }
                    } else {
                        // Catch any unrecognized custom credential type here.
                        authState.update { AuthState.Error("Unexpected type of credential") }
                        authState.update { AuthState.Unauthenticated }
                        Timber.e("Unexpected type of credential")
                    }
                } catch (e: GetCredentialCancellationException) {
                    authState.update { AuthState.Error(e.message.toString()) }
                    authState.update { AuthState.Unauthenticated }
                    // Manejo de la cancelación por parte del usuario
                    Timber.e(e, "Usuario canceló el inicio de sesión")
                    // Aquí puedes notificar al usuario, registrar el evento, etc.
                }
         */
    }

    fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        credential = result.credential

        when (credential) {
            is CustomCredential -> {
                if (credential!!.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential!!.data)

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
        // TODO: Al hacer el logout hay que comprobar el provider.
        //  para google hay que hacer `clearCredentialState()`
        //  https://developer.android.com/identity/sign-in/credential-manager-siwg#handle-sign-out

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
                            if (credential == null) {
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
                            } else {
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
        if (credential != null) {
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

/*

val request: GetCredentialRequest = Builder()
    .addCredentialOption(googleIdOption)
    .build()

coroutineScope.launch {
    try {
        val result = credentialManager.getCredential(
            request = request,
            context = activityContext,
        )
        handleSignIn(result)
    } catch (e: GetCredentialException) {
        handleFailure(e)
    }
}

fun handleSignIn(result: GetCredentialResponse) {
    // Handle the successfully returned credential.
    val credential = result.credential

    when (credential) {

        // Passkey credential
        is PublicKeyCredential -> {
            // Share responseJson such as a GetCredentialResponse on your server to
            // validate and authenticate
            responseJson = credential.authenticationResponseJson
        }

        // Password credential
        is PasswordCredential -> {
            // Send ID and password to your server to validate and authenticate.
            val username = credential.id
            val password = credential.password
        }

        // GoogleIdToken credential
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    // Use googleIdTokenCredential and extract the ID to validate and
                    // authenticate on your server.
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)
                    // You can use the members of googleIdTokenCredential directly for UX
                    // purposes, but don't use them to store or control access to user
                    // data. For that you first need to validate the token:
                    // pass googleIdTokenCredential.getIdToken() to the backend server.
                    GoogleIdTokenVerifier verifier = ... // see validation instructions
                    GoogleIdToken idToken = verifier.verify(idTokenString);
                    // To get a stable account identifier (e.g. for storing user data),
                    // use the subject ID:
                    idToken.getPayload().getSubject()
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e(TAG, "Received an invalid google id token response", e)
                }
            } else {
                // Catch any unrecognized custom credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }

        else -> {
            // Catch any unrecognized credential type here.
            Log.e(TAG, "Unexpected type of credential")
        }
    }
}*/
