package com.digitalsolution.familyfilmapp.ui.screens.login.usecases

import com.digitalsolution.familyfilmapp.BaseUseCase
import com.digitalsolution.familyfilmapp.exceptions.CustomException
import com.digitalsolution.familyfilmapp.repositories.LoginRepository
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.LoginUiState
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class CheckUserLoggedInUseCase @Inject constructor(
    private val repository: LoginRepository,
) : BaseUseCase<Unit, Flow<LoginUiState>>() {
    override suspend fun execute(parameters: Unit): Flow<LoginUiState> = channelFlow {
        // Loading
        send(
            LoginUiState().copy(
                screenState = LoginRegisterState.Login(),
                isLoading = true,
            ),
        )

        repository.getUser().catch { exception ->
            send(
                LoginUiState().copy(
                    isLogged = false,
                    errorMessage = CustomException.GenericException(
                        exception.message ?: "Google Login Error",
                    ),
                ),
            )
        }.collectLatest { result ->
            result.fold(
                onSuccess = { logged ->
                    send(
                        LoginUiState().copy(
                            isLogged = logged,
                        ),
                    )
                },
                onFailure = {
                    send(
                        LoginUiState().copy(
                            isLogged = false,
                            errorMessage = CustomException.GenericException(
                                it.message ?: "Google Login Error",
                            ),
                        ),
                    )
                },
            )
        }
    }
}
