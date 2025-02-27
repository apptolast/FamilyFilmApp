package com.apptolast.familyfilmapp.repositories

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class FirebaseAuthRepositoryImpl @Inject constructor(private val firebaseAuth: FirebaseAuth) :
    FirebaseAuthRepository {

    /**
     * Get the current user from firebase authentication.
     * It handles the auth state changes.
     */
    override fun getUser(): Flow<FirebaseUser?> = callbackFlow {
        firebaseAuth.addAuthStateListener {
            launch { send(it.currentUser) }
        }
        awaitClose()
    }

    /**
     * Login a user with email and password in firebase authentication.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     */
    override fun login(email: String, password: String): Flow<Result<FirebaseUser?>> = callbackFlow {
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
    override fun register(email: String, password: String): Flow<Result<FirebaseUser?>> = callbackFlow {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Send validation email
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            launch {
                                send(Result.success(user))
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

    override fun logOut() = firebaseAuth.signOut()

    override fun deleteAccount(email: String, password: String): Flow<Result<Boolean>> = callbackFlow {
        val user = firebaseAuth.currentUser
        if (user != null) {
            val credential = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential)
            user.delete()
                .addOnSuccessListener {
                    launch { send(Result.success(true)) }
                }
                .addOnFailureListener { exception ->
                    launch { send(Result.failure(exception)) }
                }
        } else {
            launch { send(Result.failure(Exception("No user logged in"))) }
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

    override fun recoverPassword(email: String): Flow<Result<Boolean>> = callbackFlow {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                launch { send(Result.success(it.isSuccessful)) }
            }
            .addOnFailureListener {
                launch { send(Result.failure(it)) }
            }
        awaitClose()
    }

    override fun isTokenValid(): Flow<Boolean> = callbackFlow {
        firebaseAuth.currentUser?.getIdToken(true)
            ?.addOnCompleteListener { it ->
                launch { send(true) }
            }
            ?.addOnFailureListener {
                launch { send(false) }
            }
        awaitClose()
    }
}

interface FirebaseAuthRepository {
    fun getUser(): Flow<FirebaseUser?>
    fun login(email: String, password: String): Flow<Result<FirebaseUser?>>
    fun register(email: String, password: String): Flow<Result<FirebaseUser?>>
    fun logOut()
    fun deleteAccount(email: String, password: String): Flow<Result<Boolean>>
    fun loginWithGoogle(idToken: String): Flow<Result<AuthResult>>
    fun recoverPassword(email: String): Flow<Result<Boolean>>
    fun isTokenValid(): Flow<Boolean>
}
