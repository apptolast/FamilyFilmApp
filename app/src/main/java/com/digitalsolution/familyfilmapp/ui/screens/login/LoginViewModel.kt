package com.digitalsolution.familyfilmapp.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.exceptions.CustomException.GenericException
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.CheckUserLoggedInUseCase
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.LoginEmailPassUseCase
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.LoginWithGoogleUseCase
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.RegisterUseCase
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
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginEmailPassUseCase: LoginEmailPassUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase,
    private val registerUseCase: RegisterUseCase,
    val googleSignInClient: GoogleSignInClient,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LoginUiState()
    )

    init {
        viewModelScope.launch {
            checkUserLoggedInUseCase(Unit).collectLatest { newLoginUiState ->
                _state.update {
                    newLoginUiState
                }
            }
        }
    }

    fun changeScreenState() = viewModelScope.launch {
        _state.update {
            when (it.screenState) {
                is LoginScreenState.Login -> it.copy(screenState = LoginScreenState.Register())
                is LoginScreenState.Register -> it.copy(screenState = LoginScreenState.Login())
            }
        }
    }

    fun loginOrRegister(email: String, password: String) = viewModelScope.launch {
        when (_state.value.screenState) {
            is LoginScreenState.Login -> {
                loginEmailPassUseCase(email to password)
            }

            is LoginScreenState.Register -> {
                registerUseCase(email to password)
            }
        }
            .catch { error ->
                _state.update { loginState ->
                    loginState.copy(
                        errorMessage = GenericException(error.message ?: "Login Error")
                    )
                }
            }
            .collectLatest { newLoginUIState ->
                _state.update {
                    newLoginUIState
                }
            }
    }

    fun recoverPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
    }

    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) = viewModelScope.launch {
        val account = task.result as GoogleSignInAccount
        loginWithGoogleUseCase(account.idToken!!).let { result ->
            result.collectLatest { newLoginUIState ->
                _state.update {
                    newLoginUIState
                }
            }
        }
    }

    fun homeLogOut() = viewModelScope.launch {
        checkUserLoggedInUseCase(Unit).collectLatest { newLoginUiState ->
            _state.update {
                newLoginUiState
            }
        }
    }
}
