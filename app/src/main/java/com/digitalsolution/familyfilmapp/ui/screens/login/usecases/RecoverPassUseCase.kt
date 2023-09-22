package com.digitalsolution.familyfilmapp.ui.screens.login.usecases

import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.exceptions.LoginException
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import com.digitalsolution.familyfilmapp.ui.screens.login.LoginScreenState
import com.digitalsolution.familyfilmapp.ui.screens.login.LoginUiState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class RecoverPassUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) : BaseUseCase<String, kotlinx.coroutines.flow.Flow<LoginUiState>>() {

    override suspend fun execute(parameters: String): kotlinx.coroutines.flow.Flow<LoginUiState> =
        channelFlow {


            // Loading
            send(
                LoginUiState().copy(
                    screenState = LoginScreenState.Login(),
                    isLoading = true,
                )
            )

            when {
                !parameters.isEmailValid() -> {
                    send(
                        LoginUiState().copy(
                            screenState = LoginScreenState.Login(),
                            emailErrorMessage = LoginException.EmailInvalidFormat(),
                            isLoading = false
                        )
                    )
                }

                else -> {

                    loginRepository.sendEmailRecoverPassword(parameters).catch { exception ->
                        send(
                            LoginUiState().copy(
                                screenState = LoginScreenState.Login(),
                                errorMessage = CustomException.GenericException(
                                    exception.message ?: "Send Email Error"
                                )
                            )
                        )
                    }.collectLatest { result ->
                        result.fold(
                            onSuccess = {
                                send(
                                    LoginUiState().copy(
                                        isSendEmailRecovered = it
                                    )
                                )
                            },
                            onFailure = {
                                send(
                                    LoginUiState().copy(
                                        isSendEmailRecovered = false,
                                        errorMessage = CustomException.GenericException(
                                            it.message ?: "Send Email Recover"
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