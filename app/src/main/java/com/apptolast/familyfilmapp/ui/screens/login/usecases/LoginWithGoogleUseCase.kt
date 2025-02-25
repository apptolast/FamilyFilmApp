package com.apptolast.familyfilmapp.ui.screens.login.usecases

import com.apptolast.familyfilmapp.repositories.FirebaseAuthRepository
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginState
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow

class LoginWithGoogleUseCase @Inject constructor(private val repository: FirebaseAuthRepository) :
    com.apptolast.familyfilmapp.BaseUseCase<String, Flow<LoginState>>() {

    override suspend fun execute(parameters: String): Flow<LoginState> = channelFlow {
        send(
            LoginState().copy(
                screenState = LoginRegisterState.Login(),
                isLoading = true,
            ),
        )

        repository.loginWithGoogle(parameters)
            .catch { exception ->
                send(
                    LoginState().copy(
                        screenState = LoginRegisterState.Login(),
                        isLoading = false,
                        isLogged = false,
//                        errorMessage = CustomException.GenericException(
//                            exception.message ?: "Google Login Error",
//                        ),
                    ),
                )
            }
            .collect { result ->
//                result.fold(
//                    onSuccess = { authResult ->
//                        send(
//                            LoginUiState().copy(
//                                screenState = LoginRegisterState.Register(),
//                                user = User(
//                                    id = -1,
//                                    email = authResult.user?.email ?: "",
//                                    language = "",
//                                    provider = "",
//                                ),
//                                isLogged = true,
//                                isLoading = false,
//                            ),
//                        )
//                    },
//                    onFailure = { exception ->
//                        send(
//                            LoginUiState().copy(
//                                screenState = LoginRegisterState.Login(),
//                                isLogged = false,
//                                isLoading = false,
//                                errorMessage = CustomException.GenericException(
//                                    exception.message ?: "Google Login Failure",
//                                ),
//                            ),
//                        )
//                    },
//                )
            }
    }
}
