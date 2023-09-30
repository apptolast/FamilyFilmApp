package com.digitalsolution.familyfilmapp.ui.screens.login.usecases

import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.exceptions.CustomException.GenericException
import com.digitalsolution.familyfilmapp.exceptions.LoginException.EmailInvalidFormat
import com.digitalsolution.familyfilmapp.exceptions.LoginException.PasswordInvalidFormat
import com.digitalsolution.familyfilmapp.extensions.isEmailValid
import com.digitalsolution.familyfilmapp.extensions.isPasswordValid
import com.digitalsolution.familyfilmapp.model.local.UserData
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.LoginUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class LoginEmailPassUseCase @Inject constructor(
    private val repository: LoginRepository,
) : BaseUseCase<Pair<String, String>, Flow<LoginUiState>>() {

    override suspend fun execute(parameters: Pair<String, String>): Flow<LoginUiState> =
        channelFlow {
            val (email, pass) = parameters

            // Loading
            send(
                LoginUiState().copy(
                    screenState = LoginRegisterState.Login(),
                    isLoading = true,
                )
            )

            when {

                !email.isEmailValid() && !pass.isPasswordValid() -> {
                    send(
                        LoginUiState().copy(
                            screenState = LoginRegisterState.Login(),
                            emailErrorMessage = EmailInvalidFormat(),
                            passErrorMessage = PasswordInvalidFormat(),
                            isLoading = false
                        )
                    )
                }

                !email.isEmailValid() -> {
                    send(
                        LoginUiState().copy(
                            screenState = LoginRegisterState.Login(),
                            emailErrorMessage = EmailInvalidFormat(),
                            isLoading = false
                        )
                    )
                }

                !pass.isPasswordValid() -> {
                    send(
                        LoginUiState().copy(
                            screenState = LoginRegisterState.Login(),
                            passErrorMessage = PasswordInvalidFormat(),
                            isLoading = false
                        )
                    )
                }

                else -> {
                    repository.loginEmailPass(email, pass)
                        .catch { exception ->
                            send(
                                LoginUiState().copy(
                                    screenState = LoginRegisterState.Login(),
                                    isLogged = false,
                                    isLoading = false,
                                    errorMessage = GenericException(
                                        exception.message ?: "Login Error"
                                    ),
                                )
                            )
                        }
                        .collectLatest { result ->
                            result.fold(
                                onSuccess = { authResult ->
                                    send(
                                        LoginUiState().copy(
                                            screenState = LoginRegisterState.Login(),
                                            userData = UserData(
                                                email = email,
                                                pass = pass,
                                                name = authResult.user?.displayName ?: "",
                                                photo = authResult.user?.photoUrl.toString()
                                            ),
                                            isLogged = true,
                                            isLoading = false
                                        )
                                    )
                                },
                                onFailure = { exception ->
                                    send(
                                        LoginUiState().copy(
                                            screenState = LoginRegisterState.Login(),
                                            isLogged = false,
                                            isLoading = false,
                                            errorMessage = GenericException(
                                                exception.message ?: "Login Failure"
                                            )
                                        )
                                    )
                                },
                            )
                        }
                }
            }
        }
}
