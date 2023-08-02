package com.digitalsolution.familyfilmapp.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.model.local.Login
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
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
    private val loginRepository: LoginRepository
) : ViewModel() {
    // TODO: Implement the ViewModel

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun login(email: String, password: String) = viewModelScope.launch {

        // Field Validation

        _state.update { loginUiState ->
            loginUiState.copy(isLoading = true)
        }
        loginRepository.login(email, password).collectLatest { result ->
            result.fold(
                onSuccess = { authResult ->
                    _state.update { loginUiState ->
                        loginUiState.copy(
                            login = Login(
                                email = email,
                                pass = "pass",
                                isLogin = true,
                                isRegistered = authResult.user != null
                            ),
                            isLoading = false,
                            hasError = false,
                            errorMessage = ""
                        )
                    }
                },
                onFailure = {
                    _state.update { loginUiState ->
                        loginUiState.copy(
                            login = Login(
                                email = email,
                                pass = "",
                                isLogin = false,
                                isRegistered = false
                            ),
                            isLoading = false,
                            hasError = true,
                            errorMessage = it.message ?: "Login Error"
                        )
                    }
                }
            )
        }
    }

    fun register(email: String, password: String) {
        // TODO
    }

}