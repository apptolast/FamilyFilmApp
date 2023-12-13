package com.apptolast.familyfilmapp.ui.screens.login.usecases

import com.apptolast.familyfilmapp.BaseUseCase
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.exceptions.LoginException
import com.apptolast.familyfilmapp.extensions.isEmailValid
import com.apptolast.familyfilmapp.repositories.LoginRepository
import com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassUiState
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class RecoverPassUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
) : com.apptolast.familyfilmapp.BaseUseCase<String, Flow<RecoverPassUiState>>() {

    override suspend fun execute(parameters: String): Flow<RecoverPassUiState> = channelFlow {
        send(
            RecoverPassUiState().copy(
                isDialogVisible = true,
                isLoading = false,
            ),
        )

        if (!parameters.isEmailValid()) {
            send(
                RecoverPassUiState().copy(
                    isDialogVisible = true,
                    isLoading = false,
                    emailErrorMessage = LoginException.EmailInvalidFormat(),
                ),
            )
        } else {
            send(
                RecoverPassUiState().copy(
                    isDialogVisible = true,
                    isLoading = true,
                ),
            )
            loginRepository.recoverPassword(parameters).catch { exception ->
                send(
                    RecoverPassUiState().copy(
                        isDialogVisible = false,
                        isLoading = false,
                        errorMessage = CustomException.GenericException(
                            exception.message ?: "Recover Pass Email Error",
                        ),
                    ),
                )
            }.collectLatest { result ->
                result.fold(
                    onSuccess = { firebaseResponse ->
                        send(
                            RecoverPassUiState().copy(
                                isDialogVisible = !firebaseResponse,
                                isLoading = false,
                                recoveryPassResponse = firebaseResponse,
                            ),
                        )
                    },
                    onFailure = {
                        send(
                            RecoverPassUiState().copy(
                                isDialogVisible = false,
                                isLoading = false,
                                errorMessage = CustomException.GenericException(
                                    it.message ?: "Recover Pass Email Error",
                                ),
                            ),
                        )
                    },
                )
            }
        }
    }
}
