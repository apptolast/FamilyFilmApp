package com.digitalsolution.familyfilmapp.ui.screens.login.usecases

import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.exceptions.LoginAndRegisterExceptions
import com.digitalsolution.familyfilmapp.model.local.UserData
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import com.digitalsolution.familyfilmapp.ui.screens.login.LoginScreenState
import com.digitalsolution.familyfilmapp.ui.screens.login.LoginUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: LoginRepository,
) : BaseUseCase<Pair<String, String>, Flow<LoginUiState>>() {

    override suspend fun execute(parameters: Pair<String, String>): Flow<LoginUiState> =
        channelFlow {
            val (email, pass) = parameters

            // TODO: Validate fields: email restriction and empty fields validations

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
                            emailErrorMessage = LoginAndRegisterExceptions.EmailInvalidFormat.message,
                            passErrorMessage = LoginAndRegisterExceptions.PasswordInavalidFormat.message,
                            isLoading = false
                        )
                    )
                }

                !email.isEmailValid() -> {
                    send(
                        LoginUiState().copy(
                            screenState = LoginScreenState.Register(),
                            emailErrorMessage = LoginAndRegisterExceptions.EmailInvalidFormat.message,
                            isLoading = false
                        )
                    )

                }

                !pass.isPasswordValid() -> {
                    send(
                        LoginUiState().copy(
                            screenState = LoginScreenState.Register(),
                            passErrorMessage = LoginAndRegisterExceptions.PasswordInavalidFormat.message,
                            isLoading = false
                        )
                    )

                }

                else -> {
                    repository.register(email, pass)
                        .catch { exception ->
                            Timber.tag("UseCase").d("catch: register")

                            send(
                                LoginUiState().copy(
                                    screenState = LoginScreenState.Register(),
                                    isLoading = false,
                                    errorMessage = exception.message,
                                )
                            )
                        }
                        .collectLatest { result ->
                            result.fold(
                                onSuccess = { _ ->
                                    Timber.tag("UseCase").d("onSuccess: register")

                                    send(
                                        LoginUiState().copy(
                                            screenState = LoginScreenState.Register(),
                                            userData = UserData(
                                                email = email,
                                                pass = pass
                                            ),
                                            isLoading = false
                                        )
                                    )
                                },
                                onFailure = { exception ->
                                    Timber.tag("UseCase").d("onFailure: register")

                                    send(
                                        LoginUiState().copy(
                                            screenState = LoginScreenState.Register(),
                                            isLoading = false,
                                            errorMessage = exception.message
                                        )
                                    )
                                }
                            )
                        }
                }
            }
        }
}
