package com.digitalsolution.familyfilmapp.ui.screens.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.LoginEmailPassUseCase
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.LoginWithGoogleUseCase
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.RegisterUseCase
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
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
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val googleSignInClient: GoogleSignInClient,
    private val loginRepository: LoginRepository,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LoginUiState()
    )

    /**
     * FIXME: No sé por qué me esta dando fallos y excepciones el Login diciendome que no hay recapcha
     *  o que el email esta mal formateado, etc etc, pero o consigo que muestre el error en el LaunchEffect
     *  cada vez que actualizo el loginState. El depurador parece que para en los catch y parece que tiene
     *  el mensaje de error correcto, pero luego no lo pinta en el snackbar.
     *
     *  OS LO DEJO DE TAREA A VER SI AVERIGUAIS QUE ESTÁ PASANDO.
     */
    fun login(email: String, password: String) = viewModelScope.launch {
        loginEmailPassUseCase(email to password)
            .catch { newLoginUIState ->
                _state.update { loginState ->
                    loginState.copy(
                        errorMessage = newLoginUIState.message ?: "Login Error"
                    )
                }
            }
            .collectLatest { newLoginUIState ->
                _state.update {
                    newLoginUIState
                }
            }
    }

    fun register(email: String, password: String) = viewModelScope.launch {
        registerUseCase(email to password)
            .catch { newLoginUIState ->
                _state.update { loginUiState ->
                    loginUiState.copy(
                        errorMessage = newLoginUIState.message ?: "Register Error"
                    )
                }
            }
            .collectLatest { newLoginUiState ->
                _state.update {
                    newLoginUiState
                }
            }
    }

    fun isUserLogIn(): Boolean = loginRepository.getUser() != null

    fun getGoogleSignInIntent(): Intent = googleSignInClient.signInIntent

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

}