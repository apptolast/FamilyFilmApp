package com.digitalsolution.familyfilmapp.ui.screens.login.usecases

import android.util.Patterns
import com.digitalsolution.familyfilmapp.BaseUseCase
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

            // TODO: Validate fields: email restriction and empty fields validations

            // Loading
            send(
                LoginUiState().copy(
                    screenState = LoginScreenState.Register,
                    emailErrorMessage = null,
                    passErrorMessage = null,
                    isLoading = true,
                    errorMessage = null
                )
            )

            if (Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches() && pass.validatePasswordLoginField()
            ) {
                // Do login if fields are valid
                repository.register(email, pass)
                    .catch { exception ->
                        send(
                            LoginUiState().copy(
                                screenState = LoginScreenState.Register,
                                emailErrorMessage = exception.message,
                                passErrorMessage = exception.message,
                                isLoading = false,
                                errorMessage = exception.message ?: "Login Error"
                            )
                        )
                    }
                    .collectLatest { result ->
                        result.fold(
                            onSuccess = { authResult ->
                                send(
                                    LoginUiState().copy(
                                        screenState = LoginScreenState.Register,
                                        userData = UserData(
                                            email = email,
                                            pass = pass,
                                            isLogin = true,
                                            isRegistered = authResult.user != null
                                        ),
                                        emailErrorMessage = null,
                                        passErrorMessage = null,
                                        isLoading = false,
                                        errorMessage = null
                                    )
                                )
                            },
                            onFailure = {
                                send(
                                    LoginUiState().copy(
                                        screenState = LoginScreenState.Register,
                                        userData = UserData(
                                            email = "",
                                            pass = "",
                                            isLogin = false,
                                            isRegistered = false
                                        ),
                                        emailErrorMessage = null,
                                        passErrorMessage = null,
                                        isLoading = false,
                                        errorMessage = it.message ?: "Login Error"
                                    )
                                )
                            }
                        )
                    }
            } else {
                // Loading
                send(
                    LoginUiState().copy(
                        screenState = LoginScreenState.Register,
                        emailErrorMessage = null,
                        passErrorMessage = null,
                        isLoading = true,
                        errorMessage = null
                    )
                )
            }
        }
}
