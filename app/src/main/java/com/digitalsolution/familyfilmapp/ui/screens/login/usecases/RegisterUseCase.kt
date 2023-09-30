package com.digitalsolution.familyfilmapp.ui.screens.login.usecases

import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.exceptions.LoginException.EmailInvalidFormat
import com.digitalsolution.familyfilmapp.exceptions.LoginException.PasswordInvalidFormat
import com.digitalsolution.familyfilmapp.extensions.isEmailValid
import com.digitalsolution.familyfilmapp.extensions.isPasswordValid
import com.digitalsolution.familyfilmapp.model.local.User
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.LoginUiState
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class RegisterUseCase @Inject constructor(
    private val repository: LoginRepository,
) : BaseUseCase<Pair<String, String>, Flow<LoginUiState>>() {

    override suspend fun execute(parameters: Pair<String, String>): Flow<LoginUiState> =
        channelFlow {
            val (email, pass) = parameters

            // Loading
            send(
                LoginUiState().copy(
                    screenState = LoginRegisterState.Register(),
                    isLoading = true,
                ),
            )

            when {
                !email.isEmailValid() && !pass.isPasswordValid() -> {
                    send(
                        LoginUiState().copy(
                            screenState = LoginRegisterState.Register(),
                            emailErrorMessage = EmailInvalidFormat(),
                            passErrorMessage = PasswordInvalidFormat(),
                            isLoading = false,
                        ),
                    )
                }

                !email.isEmailValid() -> {
                    send(
                        LoginUiState().copy(
                            screenState = LoginRegisterState.Register(),
                            emailErrorMessage = EmailInvalidFormat(),
                            isLoading = false,
                        ),
                    )
                }

                !pass.isPasswordValid() -> {
                    send(
                        LoginUiState().copy(
                            screenState = LoginRegisterState.Register(),
                            passErrorMessage = PasswordInvalidFormat(),
                            isLoading = false,
                        ),
                    )
                }

                else -> {
                    repository.register(email, pass)
                        .catch { exception ->
                            send(
                                LoginUiState().copy(
                                    screenState = LoginRegisterState.Register(),
                                    isLogged = false,
                                    isLoading = false,
                                    errorMessage = CustomException.GenericException(
                                        exception.message ?: "Register Error",
                                    ),
                                ),
                            )
                        }
                        .collectLatest { result ->
                            result.fold(
                                onSuccess = { _ ->
                                    send(
                                        LoginUiState().copy(
                                            screenState = LoginRegisterState.Register(),
                                            user = User(
                                                email = email,
                                                pass = pass,
                                                name = authResult.user?.displayName ?: "",
                                                photo = authResult.user?.photoUrl.toString(),
                                            ),
                                            isLogged = true,
                                            isLoading = false,
                                        ),
                                    )
                                },
                                onFailure = { exception ->
                                    send(
                                        LoginUiState().copy(
                                            screenState = LoginRegisterState.Register(),
                                            isLogged = false,
                                            isLoading = false,
                                            errorMessage = CustomException.GenericException(
                                                exception.message ?: "Register Failure",
                                            ),
                                        ),
                                    )
                                },
                            )
                        }
                }
            }
        }
}
