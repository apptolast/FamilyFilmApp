package com.digitalsolution.familyfilmapp.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

}