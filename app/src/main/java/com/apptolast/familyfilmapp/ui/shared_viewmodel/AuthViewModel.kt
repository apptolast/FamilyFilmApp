package com.apptolast.familyfilmapp.ui.shared_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.FirebaseAuthRepository
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
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
    val googleSignInClient: GoogleSignInClient,
) : ViewModel() {

    val authState: StateFlow<AuthState>
        field: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Unauthenticated)

    val screenState: StateFlow<LoginRegisterState>
        field: MutableStateFlow<LoginRegisterState> = MutableStateFlow(LoginRegisterState.Login())

    val email: StateFlow<String>
        field: MutableStateFlow<String> = MutableStateFlow("")

    val password: StateFlow<String>
        field: MutableStateFlow<String> = MutableStateFlow("")


//    val recoverPassState: StateFlow<RecoverPassState>
//        field: MutableStateFlow<RecoverPassState> = MutableStateFlow(RecoverPassState())

    init {
        viewModelScope.launch {
            authRepository.getUser()
                .catch { error ->
                    Timber.e(error, "Error getting user")
                    authState.update { AuthState.Error(error.message ?: "Error getting the user") }
                }
                .filterNotNull()
                .collectLatest { user ->
                    if (user.isEmailVerified) {
                        AuthState.Authenticated(user)
                    } else {
                        AuthState.Unauthenticated
                    }.let {
                        authState.update { it }
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

    fun register(email: String, password: String) = viewModelScope.launch {
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
                        authState.update { AuthState.Unauthenticated }
                        screenState.update { LoginRegisterState.Login() }
                    }
                    .onFailure { error ->
                        authState.update {
                            AuthState.Error(error.message ?: "Login Error")
                        }
                    }
            }
    }

//    fun recoverPassword(email: String) = viewModelScope.launch(dispatcherProvider.io()) {
//            recoverPassUseCase(email).catch { error ->
//                _recoverPassState.update {
//                    it.copy(
//                        errorMessage = GenericException(error.message ?: "Recover Pass Error"),
//                    )
//                }
//            }.collectLatest { newLoginUIState ->
//                _recoverPassState.update {
//                    newLoginUIState
//                }
//            }
//    }

//    fun updateRecoveryPasswordState(newRecoverPassState: RecoverPassState) {
//        recoverPassState.update { newRecoverPassState }
//    }

//    fun handleGoogleSignInResult(account: GoogleSignInAccount) = viewModelScope.launch(dispatcherProvider.io()) {
//            loginWithGoogleUseCase(account.idToken!!).let { result ->
//                result.collectLatest { newLoginUIState ->
//                    // User Login into our backend before update the UI state
//                    backendLogin(newLoginUIState)
//                }
//            }
//    }


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
