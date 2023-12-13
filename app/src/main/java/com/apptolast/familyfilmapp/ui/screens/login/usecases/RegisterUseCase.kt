package com.apptolast.familyfilmapp.ui.screens.login.usecases

import com.apptolast.familyfilmapp.BaseUseCase
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.exceptions.LoginException.EmailInvalidFormat
import com.apptolast.familyfilmapp.exceptions.LoginException.PasswordInvalidFormat
import com.apptolast.familyfilmapp.extensions.isEmailValid
import com.apptolast.familyfilmapp.extensions.isPasswordValid
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.LoginRepository
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginUiState
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class RegisterUseCase @Inject constructor(
    private val repository: LoginRepository,
) : com.apptolast.familyfilmapp.BaseUseCase<Pair<String, String>, Flow<LoginUiState>>() {

    override suspend fun execute(parameters: Pair<String, String>): Flow<LoginUiState> = channelFlow {
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
                            onSuccess = { authResult ->
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
