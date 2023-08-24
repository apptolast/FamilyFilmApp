package com.digitalsolution.familyfilmapp.ui.screens.login

import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.model.local.Login
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginEmailPassUseCase @Inject constructor(
    private val repository: LoginRepository,
) : BaseUseCase<Pair<String, String>, Flow<LoginUiState>>() {

    override suspend fun execute(parameters: Pair<String, String>): Flow<LoginUiState> = flow {
        val (email, pass) = parameters

        // Loading
        emit(LoginUiState().copy(isLoading = true))

        // TODO: Fields validations

        // Do login if fields are valid
        repository.loginEmailPass(email, pass).collectLatest { result ->
            result.fold(
                onSuccess = { authResult ->
                    emit(
                        LoginUiState().copy(
                            login = Login(
                                email = email,
                                pass = pass,
                                isLogin = true,
                                isRegistered = authResult.user != null
                            ),
                            isLoading = false,
                            hasError = false,
                            errorMessage = ""
                        )
                    )
                },
                onFailure = {
                    emit(
                        LoginUiState().copy(
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
                    )
                }
            )
        }
    }
}