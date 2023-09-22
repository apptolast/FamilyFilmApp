package com.digitalsolution.familyfilmapp.ui.screens.login.usecases

import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.exceptions.LoginException
import com.digitalsolution.familyfilmapp.extensions.isEmailValid
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.RecoverPassUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class RecoverPassUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) : BaseUseCase<String, Flow<RecoverPassUiState>>() {

    override suspend fun execute(parameters: String): Flow<RecoverPassUiState> = channelFlow {

        if (!parameters.isEmailValid()) {
            send(
                RecoverPassUiState().copy(
                    emailErrorMessage = LoginException.EmailInvalidFormat()
                )
            )
        } else {

            loginRepository.sendEmailRecoverPassword(parameters).catch { exception ->
                send(
                    RecoverPassUiState().copy(
                        emailErrorMessage = CustomException.GenericException(
                            exception.message ?: "Recover Pass Email Error"
                        )
                    )
                )
            }.collectLatest { result ->
                result.fold(
                    onSuccess = {
                        send(
                            RecoverPassUiState().copy(
                                isSendEmailRecovered = it
                            )
                        )
                    },
                    onFailure = {
                        send(
                            RecoverPassUiState().copy(
                                errorMessage = CustomException.GenericException(
                                    it.message ?: "Recover Pass Email Error"
                                )
                            )
                        )
                    }
                )
            }
        }
    }
}
