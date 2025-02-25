package com.apptolast.familyfilmapp.ui.shared_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        field: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Loading)

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
            authRepository.getUser().collect { user ->
                authState.value = if (user != null && user.isEmailVerified) {
                    AuthState.Authenticated(user)
                } else {
                    AuthState.Unauthenticated
                }
            }
        }


//        viewModelScope.launch(dispatcherProvider.io()) {
//            firebaseAuthRepository.userState.collect { result ->
//                result.fold(
//                    onSuccess = { firebaseUser ->
//                        loginState.update {
//                            it.copy(
//                                isLogged = firebaseUser != null,
//                                isLoading = false,
//                                email = firebaseUser?.email ?: "",
//                            )
//                        }
//                    },
//                    onFailure = { error ->
//                        loginState.update {
//                            it.copy(
//                                errorMessage = GenericException(error.message ?: "Login Error"),
//                            )
//                        }
//                    },
//                )
//            }
//        }
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
        authRepository.register(email, password)
            .catch {
                Timber.e(it)
            }
            .filterNotNull()
            .collectLatest { result ->

                result
                    .onSuccess {
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

//    private fun registerUserInBackend() = viewModelScope.launch(dispatcherProvider.io()) {
//        backendRepository.createUser().fold(
//            onSuccess = {
//                _loginState.update { loginState ->
//                    loginState.copy(
//                        isLogged = true,
//                        isLoading = false,
//                        errorMessage = null,
//                    )
//                }
//            },
//            onFailure = { error ->
//                _loginState.update { loginState ->
//                    loginState.copy(
//                        errorMessage = GenericException(error.message ?: "Register user in our backend failed"),
//                    )
//                }
//            },
//        )
//    }

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
}


sealed interface AuthState {
    object Loading : AuthState
    object Unauthenticated : AuthState
    data class Authenticated(val user: FirebaseUser) : AuthState
    data class Error(val message: String, val id: UUID = UUID.randomUUID()) : AuthState
}
