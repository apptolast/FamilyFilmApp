package com.digitalsolution.familyfilmapp.ui.screens.login.usecases

import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.model.local.UserData
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import com.digitalsolution.familyfilmapp.ui.screens.login.LoginScreenState
import com.digitalsolution.familyfilmapp.ui.screens.login.LoginUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val repository: LoginRepository,
) : BaseUseCase<String, Flow<LoginUiState>>() {

    override suspend fun execute(parameters: String): Flow<LoginUiState> = channelFlow {
        send(
            LoginUiState().copy(
                screenState = LoginScreenState.Login,
                emailErrorMessage = null,
                passErrorMessage = null,
                isLoading = true,
                errorMessage = null
            )
        )
        repository.loginWithGoogle(parameters)
            .catch { exception ->
                LoginUiState().copy(
                    screenState = LoginScreenState.Login,
                    emailErrorMessage = exception.message,
                    passErrorMessage = exception.message,
                    isLoading = false,
                    errorMessage = exception.message
                )
            }
            .collect { result ->
                // Similar al manejo de resultados en LoginEmailPassUseCase
                result.fold(
                    onSuccess = { authResult ->
                        send(
                            LoginUiState().copy(
                                screenState = LoginScreenState.Login,
                                userData = UserData(
                                    email = authResult.user?.email ?: "",
                                    pass = "",
                                    isLogin = true,
                                    isRegistered = authResult.user != null
                                ),
                                emailErrorMessage = null,
                                passErrorMessage = null,
                                isLoading = false,
                                errorMessage = ""
                            )
                        )
                    },
                    onFailure = {
                        send(
                            LoginUiState().copy(
                                screenState = LoginScreenState.Login,
                                userData = UserData(
                                    email = "",
                                    pass = "",
                                    isLogin = false,
                                    isRegistered = false
                                ),
                                emailErrorMessage = it.message,
                                passErrorMessage = it.message,
                                isLoading = false,
                                errorMessage = it.message ?: "Login Error"
                            )
                        )
                    }
                )
            }
    }
}
