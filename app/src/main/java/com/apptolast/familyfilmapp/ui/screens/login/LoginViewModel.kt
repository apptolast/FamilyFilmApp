package com.apptolast.familyfilmapp.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.exceptions.CustomException.GenericException
import com.apptolast.familyfilmapp.repositories.BackendRepository
import com.apptolast.familyfilmapp.repositories.FirebaseRepository
import com.apptolast.familyfilmapp.repositories.LocalRepository
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginUiState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassState
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(

    private val backendRepository: BackendRepository,
    private val firebaseRepository: FirebaseRepository,
    private val localRepository: LocalRepository,
//    private val loginEmailPassUseCase: LoginEmailPassUseCase,
//    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
//    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase,
//    private val registerUseCase: RegisterUseCase,
    private val dispatcherProvider: DispatcherProvider,
//    private val backendRepository: BackendRepository,
//    private val firebaseAuth: FirebaseAuth,
    val googleSignInClient: GoogleSignInClient,
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _recoverPassState = MutableStateFlow(RecoverPassState())
    val recoverPassState = _recoverPassState.asStateFlow()

    fun changeScreenState() = viewModelScope.launch(dispatcherProvider.io()) {
        _loginState.update {
            when (it.screenState) {
                is LoginRegisterState.Login -> it.copy(screenState = LoginRegisterState.Register())
                is LoginRegisterState.Register -> it.copy(screenState = LoginRegisterState.Login())
            }
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch(dispatcherProvider.io()) {
        firebaseRepository.login(email, password)
            .catch { error ->
                _loginState.update { loginState ->
                    loginState.copy(
                        errorMessage = GenericException(error.message ?: "Login Error"),
                    )
                }
            }.collectLatest { firebaseUser ->
                firebaseUser?.getIdToken(false)
                    ?.addOnSuccessListener { tokenResult ->
                        localRepository.setToken(tokenResult.token ?: "")
                    }
                    ?.addOnFailureListener { error ->
                        _loginState.update {
                            it.copy(
                                errorMessage = GenericException(error.message ?: "Save token failed"),
                            )
                        }
                    }
            }
    }

    fun register(email: String, password: String) = viewModelScope.launch(dispatcherProvider.io()) {
        firebaseRepository.register(email, password)
            .catch { error ->
                _loginState.update { loginState ->
                    loginState.copy(
                        errorMessage = GenericException(error.message ?: "Register Error"),
                    )
                }
            }.collectLatest { firebaseUser ->
                firebaseUser?.getIdToken(false)
                    ?.addOnSuccessListener { tokenResult ->
                        localRepository.setToken(tokenResult.token ?: "")
                        registerUserInBackend()
                    }
                    ?.addOnFailureListener { error ->
                        _loginState.update {
                            it.copy(
                                errorMessage = GenericException(error.message ?: "Save token failed"),
                            )
                        }
                    }
            }
    }

    private fun registerUserInBackend() = viewModelScope.launch(dispatcherProvider.io()) {
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
    }

    fun recoverPassword(email: String) = viewModelScope.launch(dispatcherProvider.io()) {
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
    }

    fun updateRecoveryPasswordState(newRecoverPassState: RecoverPassState) {
        _recoverPassState.update { newRecoverPassState }
    }

    fun handleGoogleSignInResult(account: GoogleSignInAccount) = viewModelScope.launch(dispatcherProvider.io()) {
//            loginWithGoogleUseCase(account.idToken!!).let { result ->
//                result.collectLatest { newLoginUIState ->
//                    // User Login into our backend before update the UI state
//                    backendLogin(newLoginUIState)
//                }
//            }
    }
}
