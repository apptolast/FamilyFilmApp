package com.digitalsolution.familyfilmapp.ui.screens.login

import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.model.local.UserData
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class LoginEmailPassUseCase @Inject constructor(
    private val repository: LoginRepository,
) : BaseUseCase<Pair<String, String>, Flow<LoginUiState>>() {

    override suspend fun execute(parameters: Pair<String, String>): Flow<LoginUiState> = channelFlow {
        val (email, pass) = parameters

        // TODO: Validate fields: email restriction and empty fields validations

        // Loading
        send(LoginUiState().copy(isLoading = true))

        // TODO: Fields validations

        // Do login if fields are valid
        repository.loginEmailPass(email, pass)
            .catch { exception ->
                send(
                    LoginUiState().copy(hasError = true, errorMessage = exception.message ?: "Login Error")
                )
            }
            .collectLatest { result ->
                result.fold(
                    onSuccess = { authResult ->
                        send(
                            LoginUiState().copy(
                                userData = UserData(
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
                        send(
                            LoginUiState().copy(
                                userData = UserData(
                                    email = "",
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
