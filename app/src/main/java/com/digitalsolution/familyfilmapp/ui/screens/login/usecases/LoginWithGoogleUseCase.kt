package com.digitalsolution.familyfilmapp.ui.screens.login.usecases

import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.model.local.User
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.LoginUiState
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow

class LoginWithGoogleUseCase @Inject constructor(
    private val repository: LoginRepository,
) : BaseUseCase<String, Flow<LoginUiState>>() {

    override suspend fun execute(parameters: String): Flow<LoginUiState> = channelFlow {
        send(
            LoginUiState().copy(
                screenState = LoginRegisterState.Login(),
                isLoading = true,
            ),
        )

        repository.loginWithGoogle(parameters)
            .catch { exception ->
                send(
                    LoginUiState().copy(
                        screenState = LoginRegisterState.Login(),
                        isLoading = false,
                        isLogged = false,
                        errorMessage = CustomException.GenericException(
                            exception.message ?: "Google Login Error",
                        ),
                    ),
                )
            }
            .collect { result ->
                result.fold(
                    onSuccess = { authResult ->
                        send(
                            LoginUiState().copy(
                                screenState = LoginRegisterState.Register(),
                                user = User(
                                    email = authResult.user?.email ?: "",
                                    pass = "",
                                ),
                                isLogged = true,
                                isLoading = false,
                            ),
                        )
                    },
                    onFailure = { exception ->
                        send(
                            LoginUiState().copy(
                                screenState = LoginRegisterState.Login(),
                                isLogged = false,
                                isLoading = false,
                                errorMessage = CustomException.GenericException(
                                    exception.message ?: "Google Login Failure",
                                ),
                            ),
                        )
                    },
                )
            }
    }
}
