package com.apptolast.familyfilmapp.ui.screens.login.usecases

import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.exceptions.LoginException
import com.apptolast.familyfilmapp.extensions.isEmailValid
import com.apptolast.familyfilmapp.repositories.FirebaseRepository
import com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassState
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class RecoverPassUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
) : com.apptolast.familyfilmapp.BaseUseCase<String, Flow<RecoverPassState>>() {

    override suspend fun execute(parameters: String): Flow<RecoverPassState> = channelFlow {
        send(
            RecoverPassState().copy(
                isDialogVisible = true,
                isLoading = false,
            ),
        )

        if (!parameters.isEmailValid()) {
            send(
                RecoverPassState().copy(
                    isDialogVisible = true,
                    isLoading = false,
                    emailErrorMessage = LoginException.EmailInvalidFormat(),
                ),
            )
        } else {
            send(
                RecoverPassState().copy(
                    isDialogVisible = true,
                    isLoading = true,
                ),
            )
            firebaseRepository.recoverPassword(parameters).catch { exception ->
                send(
                    RecoverPassState().copy(
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
                            RecoverPassState().copy(
                                isDialogVisible = !firebaseResponse,
                                isLoading = false,
                                recoveryPassResponse = firebaseResponse,
                            ),
                        )
                    },
                    onFailure = {
                        send(
                            RecoverPassState().copy(
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
