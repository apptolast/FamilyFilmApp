package com.apptolast.familyfilmapp.repositories

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(private val firebaseAuth: FirebaseAuth) :
    FirebaseAuthRepository {

//    override val userState: StateFlow<Result<FirebaseUser?>>
//        field: MutableStateFlow<Result<FirebaseUser?>> = MutableStateFlow(Result.success(null))

    /**
     * Login a user with email and password in firebase authentication.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     */
    override fun login(email: String, password: String): Flow<Result<FirebaseUser?>> =
        callbackFlow {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    launch {
                        val user = authResult.user
                        if (user != null && user.isEmailVerified) {
                            send(Result.success(user))
                        } else {
                            send(Result.failure(Throwable("Email not verified")))
                        }
                    }
                }
                .addOnFailureListener {
                    launch { send(Result.failure(it)) }
                }
            awaitClose()
        }

    /**
     * Register a new user with email and password in firebase authentication.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     */
    override fun register(email: String, password: String): Flow<Result<Unit>> =
        callbackFlow {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    // Send validation email
                    if (task.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser
                        user?.sendEmailVerification()?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                launch {
                                    send(Result.success(Unit))
                                    send(Result.failure(Throwable(message = "Verification email sent")))
                                }
                            } else {
                                launch { send(Result.failure(task.exception as Throwable)) }
                            }
                        }
                    } else {
                        launch { send(Result.failure(task.exception as Throwable)) }
                    }
                }
                .addOnFailureListener {
                    launch { send(Result.failure(it)) }
                }
            awaitClose()
        }

    override fun loginWithGoogle(idToken: String): Flow<Result<AuthResult>> = callbackFlow {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                launch {
                    send(Result.success(it))
                }
            }
            .addOnFailureListener {
                launch {
                    send(Result.failure(it))
                }
            }
        awaitClose()
    }

    override fun getUser(): Flow<FirebaseUser?> = callbackFlow {
        firebaseAuth.addAuthStateListener {
            launch {
                send(it.currentUser)
            }
        }
        awaitClose()
    }

    override fun recoverPassword(email: String): Flow<Result<Boolean>> = callbackFlow {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                launch {
                    send(
                        Result.success(it.isSuccessful),
                    )
                }
            }
            .addOnFailureListener {
                launch {
                    send(
                        Result.failure(it),
                    )
                }
            }
        awaitClose()
    }

    override fun checkEmailVerification(): Flow<Result<Boolean>> = callbackFlow {
        val user = FirebaseAuth.getInstance().currentUser
        user?.reload()?.addOnCompleteListener { task ->
            launch {
                if (task.isSuccessful) {
                    if (user.isEmailVerified) {
                        // Grant access, the email is verified
                        send(Result.success(user.isEmailVerified))
                    } else {
                        // The email is not verified, show a message or restrict access
                        send(Result.failure(Throwable(message = "Email not verified")))
                    }
                }
            }
        }
        awaitClose()
    }

    override fun getUserId(): String? = firebaseAuth.currentUser?.uid
}

interface FirebaseAuthRepository {
//    val userState: StateFlow<Result<FirebaseUser?>>

    fun login(email: String, password: String): Flow<Result<FirebaseUser?>>
    fun register(email: String, password: String): Flow<Result<Unit>>
    fun loginWithGoogle(idToken: String): Flow<Result<AuthResult>>
    fun getUser(): Flow<FirebaseUser?>
    fun recoverPassword(email: String): Flow<Result<Boolean>>
    fun checkEmailVerification(): Flow<Result<Boolean>>
    fun getUserId(): String?
}
