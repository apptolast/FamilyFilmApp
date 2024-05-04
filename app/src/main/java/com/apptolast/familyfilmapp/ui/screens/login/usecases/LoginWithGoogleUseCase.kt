package com.apptolast.familyfilmapp.ui.screens.login.usecases

import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.FirebaseRepository
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginUiState
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow

class LoginWithGoogleUseCase @Inject constructor(
    private val repository: FirebaseRepository,
) : com.apptolast.familyfilmapp.BaseUseCase<String, Flow<LoginUiState>>() {

    override suspend fun execute(parameters: String): Flow<LoginUiState> = channelFlow {
        send(
            LoginUiState().copy(
                screenState = LoginRegisterState.Login(),
                isLoading = true,
            ),
        )

        repository.loginWithGoogle(parameters)
            .catch { exception ->
                send(
                    LoginUiState().copy(
                        screenState = LoginRegisterState.Login(),
                        isLoading = false,
                        isLogged = false,
                        errorMessage = CustomException.GenericException(
                            exception.message ?: "Google Login Error",
                        ),
                    ),
                )
            }
            .collect { result ->
                result.fold(
                    onSuccess = { authResult ->
                        send(
                            LoginUiState().copy(
                                screenState = LoginRegisterState.Register(),
                                user = User(
                                    id = authResult.user?.uid ?: "",
                                    email = authResult.user?.email ?: "",
                                    pass = "",
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
                                screenState = LoginRegisterState.Login(),
                                isLogged = false,
                                isLoading = false,
                                errorMessage = CustomException.GenericException(
                                    exception.message ?: "Google Login Failure",
                                ),
                            ),
                        )
                    },
                )
            }
    }
}
