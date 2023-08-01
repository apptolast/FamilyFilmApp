package com.digitalsolution.familyfilmapp.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import com.digitalsolution.familyfilmapp.state.CustomResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {
    // TODO: Implement the ViewModel

    private val state = MutableStateFlow<CustomResult?>(null)

    val text: Flow<String> = state.map {
        when (it) {
            is CustomResult.InProgress -> "In progress: ${it.task}%"
            is CustomResult.IsError -> "Error: ${it.task}%"
            is CustomResult.Complete.Success<*> -> "Done"
            is CustomResult.Complete.Failed -> "Failed: ${it.error.localizedMessage}"
            else -> "otra"
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginRepository.login(email, password).collectIndexed { _, value: CustomResult ->
                when (value) {
                    is CustomResult.IsError -> value.task
                    is CustomResult.InProgress -> value.task
                    else -> null
                }
                state.value = value
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            loginRepository.register(email, password).collectIndexed { _, value: CustomResult ->
                when (value) {
                    is CustomResult.IsError -> value.task
                    is CustomResult.InProgress -> value.task
                    else -> null
                }
                state.value = value
            }
        }
    }

}