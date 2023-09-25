package com.digitalsolution.familyfilmapp.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.exceptions.CustomException.GenericException
import com.digitalsolution.familyfilmapp.exceptions.LoginException
import com.digitalsolution.familyfilmapp.repositories.BackendRepository
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.LoginUiState
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.RecoverPassUiState
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.CheckUserLoggedInUseCase
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.LoginEmailPassUseCase
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.LoginWithGoogleUseCase
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.RecoverPassUseCase
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.RegisterUseCase
import com.digitalsolution.familyfilmapp.utils.DispatcherProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginEmailPassUseCase: LoginEmailPassUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase,
    private val registerUseCase: RegisterUseCase,
    private val recoverPassUseCase: RecoverPassUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val backendRepository: BackendRepository,
    private val firebaseAuth: FirebaseAuth,
    val googleSignInClient: GoogleSignInClient,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LoginUiState()
    )

    private val _recoverPasswordState = MutableStateFlow(RecoverPassUiState())
    val recoverPassUIState = _recoverPasswordState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RecoverPassUiState()
    )

    init {
        viewModelScope.launch(dispatcherProvider.io()) {
            checkUserLoggedInUseCase(Unit).collectLatest { newLoginUiState ->
                backendLogin(newLoginUiState)
            }
        }
    }

    fun changeScreenState() = viewModelScope.launch(dispatcherProvider.io()) {
        _state.update {
            when (it.screenState) {
                is LoginRegisterState.Login -> it.copy(screenState = LoginRegisterState.Register())
                is LoginRegisterState.Register -> it.copy(screenState = LoginRegisterState.Login())
            }
        }
    }

    fun loginOrRegister(email: String, password: String) = viewModelScope.launch(dispatcherProvider.io()) {
        when (_state.value.screenState) {
            is LoginRegisterState.Login -> {
                loginEmailPassUseCase(email to password)
            }

            is LoginRegisterState.Register -> {
                registerUseCase(email to password)
            }
        }.catch { error ->
            _state.update { loginState ->
                loginState.copy(
                    errorMessage = GenericException(error.message ?: "Login Error")
                )
            }
        }.collectLatest { newLoginUIState ->
            // User Login into our backend before update the UI state
            backendLogin(newLoginUIState)
        }
    }

    fun recoverPassword(email: String) = viewModelScope.launch(dispatcherProvider.io()) {
        recoverPassUseCase(email).catch { error ->
            _recoverPasswordState.update {
                it.copy(
                    errorMessage = GenericException(error.message ?: "Recover Pass Error")
                )
            }
        }.collectLatest { newLoginUIState ->
            _recoverPasswordState.update {
                newLoginUIState
            }
        }
    }

    fun updateRecoveryPasswordState(newRecoverPassUiState: RecoverPassUiState) {
        _recoverPasswordState.update { newRecoverPassUiState }
    }

    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) = viewModelScope.launch(dispatcherProvider.io()) {
        val account = task.result as GoogleSignInAccount
        loginWithGoogleUseCase(account.idToken!!).let { result ->
            result.collectLatest { newLoginUIState ->
                // User Login into our backend before update the UI state
                backendLogin(newLoginUIState)
            }
        }
    }

    /**
     * Method to login the user into our backend after the firebase login in order to retrieve the token
     * to authenticate the requests in our backend
     *
     * @param newLoginUIState Valid `LoginUIState` retrieved from firebase
     */
    private suspend fun backendLogin(
        newLoginUIState: LoginUiState
    ) {
        if (newLoginUIState.isLogged) {
            when (_state.value.screenState) {
                is LoginRegisterState.Login -> {
                    backendRepository.login(
                        firebaseAuth.currentUser!!.email!!,
                        firebaseAuth.currentUser!!.uid
                    )
                }

                is LoginRegisterState.Register -> {
                    backendRepository.register(
                        firebaseAuth.currentUser!!.email!!,
                        firebaseAuth.currentUser!!.uid
                    )
                }
            }.fold(
                onSuccess = {
                    // Update the UI state and navigate to Home
                    _state.update { newLoginUIState }
                },
                onFailure = { _ ->

                    // This is for the edge case in which the user is logged in in firebase
                    // but the user is not register in our backend due to an error.
                    // In this case we will try to register the user again in our backend.
                    backendRepository.register(
                        firebaseAuth.currentUser!!.email!!,
                        firebaseAuth.currentUser!!.uid
                    ).fold(
                        onSuccess = {
                            Timber.w("User edge case that needs to be register again")
                            _state.update { newLoginUIState }
                        },
                        onFailure = {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = LoginException.BackendLogin()
                                )
                            }
                        }
                    )
                }
            )
        } else {
            _state.update { newLoginUIState }
        }
    }
}

