package com.digitalsolution.familyfilmapp.ui.screens.recommend.usecases
//
// import com.digitalsolution.familyfilmapp.BaseUseCase
// import com.digitalsolution.familyfilmapp.exceptions.CustomException
// import com.digitalsolution.familyfilmapp.model.local.UserData
// import com.digitalsolution.familyfilmapp.repositories.FakeRepositoryImpl
// import com.digitalsolution.familyfilmapp.repositories.LoginRepository
// import com.digitalsolution.familyfilmapp.ui.screens.recommend.FilmUiState
// import kotlinx.coroutines.flow.Flow
// import kotlinx.coroutines.flow.catch
// import kotlinx.coroutines.flow.channelFlow
// import kotlinx.coroutines.flow.collectLatest
// import javax.inject.Inject
//
// class FilmsListUseCase @Inject constructor(
//    private val repository: FakeRepositoryImpl,
// ) : BaseUseCase<Pair<String, String>, Flow<FilmUiState>>() {
//
// //    TODO: How to parameters should be here? 0.0
//    override suspend fun execute(parameters: Pair<String, String>): Flow<FilmUiState> =
//        channelFlow {
//            val (email, pass) = parameters
//
//            // Loading
//            send(
//                FilmUiState().copy(
//                    isLoading = true
//                )
//            )
//
//            repository.register(email, pass)
//                .catch { exception ->
//                    send(
//                        FilmUiState().copy(
//                            isLogged = false,
//                            isLoading = false,
//                            errorMessage = CustomException.GenericException(
//                                exception.message ?: "Error"
//                            ),
//                        )
//                    )
//                }
//                .collectLatest { result ->
//                    result.fold(
//                        onSuccess = { authResult ->
//                            send(
//                                FilmUiState().copy(
//                                    userData = UserData(
//                                        email = email,
//                                        pass = pass
//                                    ),
//                                    isLogged = true,
//                                    isLoading = false
//                                )
//                            )
//                        },
//                        onFailure = { exception ->
//                            send(
//                                FilmUiState().copy(
//                                    isLogged = false,
//                                    isLoading = false,
//                                    errorMessage = CustomException.GenericException(
//                                        exception.message ?: "Failure"
//                                    )
//                                )
//                            )
//                        }
//                    )
//                }
//        }
// }
