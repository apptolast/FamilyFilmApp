package com.digitalsolution.familyfilmapp.ui.screens.login.usecases

import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.exceptions.LoginException
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import com.digitalsolution.familyfilmapp.ui.screens.login.RecoverPassUIState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class RecoverPassUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) : BaseUseCase<String, kotlinx.coroutines.flow.Flow<RecoverPassUIState>>() {

    override suspend fun execute(parameters: String): kotlinx.coroutines.flow.Flow<RecoverPassUIState> =
        channelFlow {


            if (!parameters.isEmailValid()) {
                send(
                    RecoverPassUIState().copy(
                        emailErrorMessage = LoginException.EmailInvalidFormat()
                    )
                )
            } else {

                loginRepository.sendEmailRecoverPassword(parameters).catch { exception ->
                    send(
                        RecoverPassUIState().copy(
                            emailErrorMessage = CustomException.GenericException(
                                exception.message ?: "Recover Pass Email Error"
                            )
                        )
                    )
                }.collectLatest { result ->
                    result.fold(
                        onSuccess = {
                            send(
                                RecoverPassUIState().copy(
                                    isSendEmailRecovered = it
                                )
                            )
                        },
                        onFailure = {
                            send(
                                RecoverPassUIState().copy(
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
