package com.digitalsolution.familyfilmapp.ui.screens.login.usecases

import android.util.Patterns
import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.exceptions.LoginAndRegisterExceptions
import com.digitalsolution.familyfilmapp.model.local.UserData
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import com.digitalsolution.familyfilmapp.ui.screens.login.LoginScreenState
import com.digitalsolution.familyfilmapp.ui.screens.login.LoginUiState
import kotlinx.coroutines.delay
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

            // TODO: Validate fields: email restriction and empty fields validations

            // Loading
            send(
                LoginUiState().copy(
                    screenState = LoginScreenState.Register,
                    isLoading = true
                )
            )

            delay(1000)

            when {

                email.isBlank() && pass.isBlank() -> {
                    send(
                        LoginUiState().copy(
                            screenState = LoginScreenState.Register,
                            emailErrorMessage = LoginAndRegisterExceptions.EmailBlank.message,
                            passErrorMessage = LoginAndRegisterExceptions.PassBlank.message,
                            isLoading = false
                        )
                    )
                }

                !Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches() && !pass.isPasswordValid() -> {
                    send(
                        LoginUiState().copy(
                            screenState = LoginScreenState.Register,
                            emailErrorMessage = LoginAndRegisterExceptions.EmailInvalidFormat.message,
                            passErrorMessage = LoginAndRegisterExceptions.PasswordInavalidFormat.message,
                            isLoading = false
                        )
                    )
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    send(
                        LoginUiState().copy(
                            screenState = LoginScreenState.Register,
                            emailErrorMessage = LoginAndRegisterExceptions.EmailInvalidFormat.message,
                            isLoading = false
                        )
                    )

                }

                !pass.isPasswordValid() -> {
                    send(
                        LoginUiState().copy(
                            screenState = LoginScreenState.Register,
                            passErrorMessage = LoginAndRegisterExceptions.PasswordInavalidFormat.message,
                            isLoading = false
                        )
                    )

                }

                else -> {
                    repository.register(email, pass)
                        .catch { exception ->
                            send(
                                LoginUiState().copy(
                                    screenState = LoginScreenState.Register,
                                    isLoading = false,
                                    errorMessage = exception.message,
                                )
                            )
                        }
                        .collectLatest { result ->
                            result.fold(
                                onSuccess = { AuthResult ->
                                    send(
                                        LoginUiState().copy(
                                            screenState = LoginScreenState.Register,
                                            userData = UserData(
                                                email = email,
                                                pass = pass,
                                                isLogin = true,
                                                isRegistered = false
                                            ),
                                            isLoading = false
                                        )
                                    )
                                },
                                onFailure = { exception ->
                                    send(
                                        LoginUiState().copy(
                                            screenState = LoginScreenState.Register,
                                            isLoading = false,
                                            errorMessage = exception.message
                                        )
                                    )
                                }
                            )
                        }

                }
            }

            // Loading
            send(
                LoginUiState().copy(
                    screenState = LoginScreenState.Register,
                    isLoading = false
                )
            )


        }
}
