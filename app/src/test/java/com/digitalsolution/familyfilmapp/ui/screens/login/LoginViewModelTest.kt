package com.digitalsolution.familyfilmapp.ui.screens.login

//
// @RunWith(MockitoJUnitRunner::class)
// class LoginViewModelTest {
//
//    private lateinit var viewModel: LoginViewModel
//
//    // Set the main coroutines dispatcher for unit testing.
//    @get:Rule
//    internal var coroutineRule = MainDispatcherRule()
//
//    @Mock
//    private lateinit var loginEmailPassUseCase: LoginEmailPassUseCase
//
//    @Mock
//    private lateinit var loginWithGoogleUseCase: LoginWithGoogleUseCase
//
//    @Mock
//    private lateinit var checkUserLoggedInUseCase: CheckUserLoggedInUseCase
//
//    @Mock
//    private lateinit var registerUseCase: RegisterUseCase
//
//    @Mock
//    private lateinit var recoverPassUseCase: RecoverPassUseCase
//
//    @Mock
//    private lateinit var backendRepository: BackendRepository
//
//    @Mock
//    private lateinit var firebaseAuth: FirebaseAuth
//
//    @Mock
//    private lateinit var googleSignInClient: GoogleSignInClient
//
//    @Before
//    fun setUp() {
//        viewModel = LoginViewModel(
//            loginEmailPassUseCase,
//            loginWithGoogleUseCase,
//            checkUserLoggedInUseCase,
//            registerUseCase,
//            recoverPassUseCase,
//            coroutineRule.testDispatcherProvider,
//            backendRepository,
//            firebaseAuth,
//            googleSignInClient,
//        )
//    }
//
//    @Test
//    fun `LoginViewModel - Sign In User Pass provider - Success`() = runTest {
//        // Arrange
//        val email = "email"
//        val password = "pass"
//
//        whenever(checkUserLoggedInUseCase(Unit)).thenReturn(
//            channelFlow {
//                send(
//                    LoginUiState().copy(
//                        screenState = LoginRegisterState.Login(),
//                        isLogged = false,
//                        isLoading = false,
//                    ),
//                )
//                awaitClose()
//            },
//        )
//
//        whenever(loginEmailPassUseCase(any())).thenReturn(
//            channelFlow {
//                send(
//                    LoginUiState().copy(
//                        screenState = LoginRegisterState.Login(),
//                        user = User(
//                            email = email,
//                            pass = password,
//                        ),
//                        isLogged = false,
//                        isLoading = false,
//                    ),
//                )
//                awaitClose()
//            },
//        )
//
//        whenever(backendRepository.login(any(), any())).thenReturn(
//            Result.success(Unit),
//        )
//
//        // Assert
//        val job = launch {
//            viewModel.state.test {
//                awaitItem().let {
//                    assertThat(it.user.email).isEqualTo(email)
//                    assertThat(it.user.pass).isEqualTo(password)
//                }
//                cancelAndConsumeRemainingEvents()
//            }
//        }
//
//        // Act
//        viewModel.loginOrRegister(email, password)
//
//        job.join()
//        job.cancel()
//    }
//
//    @Test
//    fun `LoginViewModel - Register User Pass provider - Success`() = runTest {
//        // Arrange
//        val email = "email"
//        val password = "pass"
//
//        viewModel.changeScreenState()
//
//        whenever(registerUseCase(any())).thenReturn(
//            channelFlow {
//                send(
//                    LoginUiState().copy(
//                        screenState = LoginRegisterState.Register(),
//                        user = User(
//                            email = email,
//                            pass = password,
//                        ),
//                        isLogged = false,
//                        isLoading = false,
//                    ),
//                )
//                awaitClose()
//            },
//        )
//
//        whenever(backendRepository.register(any(), any())).thenReturn(
//            Result.success(Unit),
//        )
//
//        // Assert
//        val job = launch {
//            viewModel.state.test {
//                awaitItem().let {
//                    assertThat(it.user.email).isEqualTo(email)
//                    assertThat(it.user.pass).isEqualTo(password)
//                }
//                cancelAndConsumeRemainingEvents()
//            }
//        }
//
//        // Act
//        viewModel.loginOrRegister(email, password)
//
//        job.join()
//        job.cancel()
//    }
//
//    @Test
//    fun `LoginViewModel - Login User Pass - Receive catch exception`() = runTest {
//        // Arrange
//        val email = "email"
//        val password = "pass"
//        val errorMessage = "Error"
//
//        whenever(loginEmailPassUseCase(any())).thenReturn(
//            channelFlow {
//                throw Exception(errorMessage)
//            },
//        )
//
//        // Assert
//        val job = launch {
//            viewModel.state.test {
//                assertThat(awaitItem().errorMessage?.error).isEqualTo(errorMessage)
//                cancelAndConsumeRemainingEvents()
//            }
//        }
//
//        // Act
//        viewModel.loginOrRegister(email, password)
//
//        job.join()
//        job.cancel()
//    }
//
//    @Test
//    fun `LoginViewModel - Change screen state between Login and Register`() = runTest {
//        // Arrange
//        whenever(checkUserLoggedInUseCase(Unit)).thenReturn(
//            channelFlow {
//                send(
//                    LoginUiState().copy(
//                        screenState = LoginRegisterState.Login(),
//                        isLogged = false,
//                        isLoading = false,
//                    ),
//                )
//                awaitClose()
//            },
//        )
//
//        // Assert
//        val job = launch {
//            viewModel.state.test {
//                assertThat(awaitItem().screenState).isEqualTo(LoginRegisterState.Register())
//                assertThat(awaitItem().screenState).isEqualTo(LoginRegisterState.Login())
//
//                cancelAndConsumeRemainingEvents()
//            }
//        }
//
//        // Act
//        viewModel.changeScreenState()
//        viewModel.changeScreenState()
//
//        job.join()
//        job.cancel()
//    }
// }
