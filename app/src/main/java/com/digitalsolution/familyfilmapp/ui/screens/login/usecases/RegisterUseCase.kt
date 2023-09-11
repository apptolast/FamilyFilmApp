package com.digitalsolution.familyfilmapp.ui.screens.login.usecases

import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.exceptions.LoginException.EmailInvalidFormat
import com.digitalsolution.familyfilmapp.exceptions.LoginException.PasswordInvalidFormat
import com.digitalsolution.familyfilmapp.model.local.UserData
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import com.digitalsolution.familyfilmapp.ui.screens.login.LoginScreenState
import com.digitalsolution.familyfilmapp.ui.screens.login.LoginUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: LoginRepository,
) : BaseUseCase<Pair<String, String>, Flow<LoginUiState>>() {

    override suspend fun execute(parameters: Pair<String, String>): Flow<LoginUiState> =
        channelFlow {
            val (email, pass) = parameters

            // Loading
            send(
                LoginUiState().copy(
                    screenState = LoginScreenState.Register(),
                    isLoading = true
                )
            )

            when {

                !email.isEmailValid() && !pass.isPasswordValid() -> {
                    send(
                        LoginUiState().copy(
                            screenState = LoginScreenState.Register(),
                            emailErrorMessage = EmailInvalidFormat(),
                            passErrorMessage = PasswordInvalidFormat(),
                            isLoading = false
                        )
                    )
                }

                !email.isEmailValid() -> {
                    send(
                        LoginUiState().copy(
                            screenState = LoginScreenState.Register(),
                            emailErrorMessage = EmailInvalidFormat(),
                            isLoading = false
                        )
                    )

                }

                !pass.isPasswordValid() -> {
                    send(
                        LoginUiState().copy(
                            screenState = LoginScreenState.Register(),
                            passErrorMessage = PasswordInvalidFormat(),
                            isLoading = false
                        )
                    )

                }

                else -> {
                    repository.register(email, pass)
                        .catch { exception ->
                            send(
                                LoginUiState().copy(
                                    screenState = LoginScreenState.Register(),
                                    isLogged = false,
                                    isLoading = false,
                                    errorMessage = CustomException.GenericException(
                                        exception.message ?: "Register Error"
                                    ),
                                )
                            )
                        }
                        .collectLatest { result ->
                            result.fold(
                                onSuccess = { authResult ->
                                    send(
                                        LoginUiState().copy(
                                            screenState = LoginScreenState.Register(),
                                            userData = UserData(
                                                email = email,
                                                pass = pass
                                            ),
                                            isLogged = true,
                                            isLoading = false
                                        )
                                    )
                                },
                                onFailure = { exception ->
                                    send(
                                        LoginUiState().copy(
                                            screenState = LoginScreenState.Register(),
                                            isLogged = false,
                                            isLoading = false,
                                            errorMessage = CustomException.GenericException(
                                                exception.message ?: "Register Failure"
                                            )
                                        )
                                    )
                                }
                            )
                        }
                }
            }
        }
}
