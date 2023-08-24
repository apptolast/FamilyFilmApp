package com.digitalsolution.familyfilmapp.ui.screens.login


import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.model.local.Login
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val repository: LoginRepository,
) : BaseUseCase<String, Flow<LoginUiState>>() {

    override suspend fun execute(parameters: String): Flow<LoginUiState> = channelFlow {
        send(LoginUiState().copy(isLoading = true))
        repository.loginWithGoogle(parameters).collect() { result ->
            // Similar al manejo de resultados en LoginEmailPassUseCase
            result.fold(
                onSuccess = { authResult ->
                    send(
                        LoginUiState().copy(
                            login = Login(
                                email = authResult.user?.email ?: "",
                                pass = "",
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
                            login = Login(
                                email = "error",
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
