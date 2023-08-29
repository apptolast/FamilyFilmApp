package com.digitalsolution.familyfilmapp.ui.screens.login.usecases

import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.model.local.UserData
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import com.digitalsolution.familyfilmapp.ui.screens.login.LoginUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val repository: LoginRepository,
) : BaseUseCase<String, Flow<LoginUiState>>() {

    override suspend fun execute(parameters: String): Flow<LoginUiState> = channelFlow {
        send(LoginUiState().copy(isLoading = true))
        repository.loginWithGoogle(parameters).collect { result ->
            // Similar al manejo de resultados en LoginEmailPassUseCase
            result.fold(
                onSuccess = { authResult ->
                    send(
                        LoginUiState().copy(
                            userData = UserData(
                                email = authResult.user?.email ?: "",
                                pass = "",
                                isLogin = true,
                                isRegistered = authResult.user != null
                            ),
                            isLoading = false,
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
                            errorMessage = it.message ?: "Login Error"
                        )
                    )
                }
            )
        }
    }
}
