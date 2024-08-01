package com.apptolast.familyfilmapp.ui.screens.login

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.exceptions.CustomException.GenericException
import com.apptolast.familyfilmapp.exceptions.LoginException.Register
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.BackendRepository
import com.apptolast.familyfilmapp.repositories.FirebaseRepository
import com.apptolast.familyfilmapp.repositories.LocalRepository
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginUiState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassState
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val repository: Repository,
    private val credentialManager: CredentialManager,
    private val credentialRequest: GetCredentialRequest,
//    private val loginEmailPassUseCase: LoginEmailPassUseCase,
//    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
//    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase,
//    private val registerUseCase: RegisterUseCase,
    private val dispatcherProvider: DispatcherProvider,
//    private val backendRepository: BackendRepository,
//    private val firebaseAuth: FirebaseAuth,
    val googleSignInClient: GoogleSignInClient,
) : ViewModel() {

    val loginState: StateFlow<LoginUiState>
        field: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())

    val recoverPassState: StateFlow<RecoverPassState>
        field: MutableStateFlow<RecoverPassState> = MutableStateFlow(RecoverPassState())

    init {
        viewModelScope.launch(dispatcherProvider.io()) {
            firebaseAuthRepository.userState.collect { result ->
                result.fold(
                    onSuccess = { firebaseUser ->
                        loginState.update {
                            it.copy(
                                isLogged = firebaseUser != null,
                                isLoading = false,
                                email = firebaseUser?.email ?: "",
                            )
                        }
                    },
                    onFailure = { error ->
                        loginState.update {
                            it.copy(
                                errorMessage = GenericException(error.message ?: "Login Error"),
                            )
                        }
                    },
                )
            }
        }
    }

    fun changeScreenState() = viewModelScope.launch(dispatcherProvider.io()) {
        loginState.update {
            when (it.screenState) {
                is LoginRegisterState.Login -> it.copy(screenState = LoginRegisterState.Register())
                is LoginRegisterState.Register -> it.copy(screenState = LoginRegisterState.Login())
            }
        }
    }

    fun login(email: String, password: String) = firebaseAuthRepository.login(email, password)

    fun register(email: String, password: String) = viewModelScope.launch {
        firebaseAuthRepository.register(email, password)
            .catch {
                Timber.e(it)
            }
            .filterNotNull()
            .collectLatest { firebaseUser ->
                loginState.update {
                    it.copy(
                        isLogged = true,
                        isLoading = false,
                        errorMessage = null,
                        email = firebaseUser.email ?: "",
                    )
                }

                // Save the user in firestore database
                repository.createUser(
                    user = User().copy(
                        id = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        language = Locale.getDefault().language,
                    ),
                    success = {},
                    failure = { error ->
                        loginState.update {
                            it.copy(
                                errorMessage = Register(
                                    error.message ?: "Register user in our backend failed"
                                ),
                            )
                        }
                    },
                )
            }
    }

    private fun registerUserInBackend() = viewModelScope.launch(dispatcherProvider.io()) {
//        backendRepository.createUser().fold(
//            onSuccess = {
//                _loginState.update { loginState ->
//                    loginState.copy(
//                        isLogged = true,
//                        isLoading = false,
//                        errorMessage = null,
//                    )
//                }
//            },
//            onFailure = { error ->
//                _loginState.update { loginState ->
//                    loginState.copy(
//                        errorMessage = GenericException(error.message ?: "Register user in our backend failed"),
//                    )
//                }
//            },
//        )
    }

    fun recoverPassword(email: String) = viewModelScope.launch(dispatcherProvider.io()) {
//            recoverPassUseCase(email).catch { error ->
//                _recoverPassState.update {
//                    it.copy(
//                        errorMessage = GenericException(error.message ?: "Recover Pass Error"),
//                    )
//                }
//            }.collectLatest { newLoginUIState ->
//                _recoverPassState.update {
//                    newLoginUIState
//                }
//            }
    }

    fun updateRecoveryPasswordState(newRecoverPassState: RecoverPassState) {
        recoverPassState.update { newRecoverPassState }
    }

    fun handleSignIn(context: Context) = viewModelScope.launch {
        // Handle the successfully returned credential.


        try {
            // Código para iniciar el intento de autenticación
            val credentialResponse = credentialManager.getCredential(
                request = credentialRequest,
                context = context,
            )

            val credential = credentialResponse.credential

            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    // Use googleIdTokenCredential and extract id to validate and
                    // authenticate on your server.
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)

                    loginWithGoogleUseCase(googleIdTokenCredential.idToken).let { result ->
                        result.collectLatest { newLoginUIState ->
                            _loginState.update {
                                newLoginUIState
                            }
                        }
                    }
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e("TAG", "Received an invalid google id token response", e)
                }
            } else {
                // Catch any unrecognized custom credential type here.
                Log.e("TAG", "Unexpected type of credential")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.e("AuthError", e.message, e)
        } catch (e: Exception) {
            Log.e("AuthError", "Error inesperado durante la autenticación.", e)
        }


    }
}
