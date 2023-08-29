package com.digitalsolution.familyfilmapp.ui.screens.login.usecases

import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.model.local.UserData
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
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
            send(LoginUiState().copy(isLoading = true))

            // TODO: Fields validations

            // Do login if fields are valid
            repository.register(email, pass)
                .catch { exception ->
                    send(
                        LoginUiState().copy(errorMessage = exception.message ?: "Login Error")
                    )
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { authResult ->
                            send(
                                LoginUiState().copy(
                                    userData = UserData(
                                        email = email,
                                        pass = pass,
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
