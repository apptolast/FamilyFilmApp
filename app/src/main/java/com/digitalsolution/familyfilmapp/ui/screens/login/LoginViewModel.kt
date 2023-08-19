package com.digitalsolution.familyfilmapp.ui.screens.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginEmailPassUseCase: LoginEmailPassUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val googleSignInClient: GoogleSignInClient
) : ViewModel() {
    // TODO: Implement the ViewModel

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun login(email: String, password: String) = viewModelScope.launch {
        loginEmailPassUseCase(email to password).let { result ->
            result.collectLatest {
                _state.update { it }
            }
        }
    }

    fun register(email: String, password: String) {
        // TODO
    }

    fun googleSignIn(): Intent {
        return googleSignInClient.signInIntent
    }

    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            viewModelScope.launch {
                loginWithGoogleUseCase(account.idToken!!).let { result ->
                    result.collectLatest {
                        _state.update { it }
                    }
                }
            }
        } catch (e: ApiException) {
            // Manejar el error
        }
    }


}