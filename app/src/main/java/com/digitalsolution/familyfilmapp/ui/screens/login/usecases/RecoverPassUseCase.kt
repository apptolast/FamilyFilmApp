package com.digitalsolution.familyfilmapp.ui.screens.login.usecases

import androidx.compose.runtime.mutableStateOf
import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.exceptions.LoginException
import com.digitalsolution.familyfilmapp.extensions.isEmailValid
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.RecoverPassUiState
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class RecoverPassUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
) : BaseUseCase<String, Flow<RecoverPassUiState>>() {

    override suspend fun execute(parameters: String): Flow<RecoverPassUiState> = channelFlow {
        send(
            RecoverPassUiState().copy(
                isDialogVisible = mutableStateOf(true),
                isLoading = false,
            ),
        )

        if (!parameters.isEmailValid()) {
            send(
                RecoverPassUiState().copy(
                    isDialogVisible = mutableStateOf(true),
                    isLoading = false,
                    emailErrorMessage = LoginException.EmailInvalidFormat(),
                ),
            )
        } else {
            send(
                RecoverPassUiState().copy(
                    isDialogVisible = mutableStateOf(true),
                    isLoading = true,
                ),
            )
            loginRepository.recoverPassword(parameters).catch { exception ->
                send(
                    RecoverPassUiState().copy(
                        isDialogVisible = mutableStateOf(false),
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
                                isDialogVisible = mutableStateOf(!firebaseResponse),
                                isLoading = false,
                                recoveryPassResponse = firebaseResponse,
                            ),
                        )
                    },
                    onFailure = {
                        send(
                            RecoverPassUiState().copy(
                                isDialogVisible = mutableStateOf(false),
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
