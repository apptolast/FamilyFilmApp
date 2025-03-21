package com.apptolast.familyfilmapp.repositories

import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.toDomainUserModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(private val firebaseAuth: FirebaseAuth) : FirebaseAuthRepository {

    val verifiedAccount: Flow<Boolean> = flow {
        while (true) {
            val verified = verifyEmailIsVerified()
            emit(verified)
            delay(1000)
        }
    }

    /**
     * Get the current user from firebase authentication.
     * It handles the auth state changes.
     */
    override fun getUser(): Flow<FirebaseUser?> = callbackFlow {
        firebaseAuth.addAuthStateListener { authStateResult ->
            authStateResult.currentUser.let { firebaseUser ->
                launch { send(firebaseUser) }
            }
        }
        awaitClose()
    }

    /**
     * Login a user with email and password in firebase authentication.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     */
    override fun login(email: String, password: String): Flow<Result<User?>> = callbackFlow {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                launch {
                    val user = authResult.user
                    if (user != null && user.isEmailVerified) {
                        send(Result.success(user.toDomainUserModel()))
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
    override fun register(email: String, password: String): Flow<Result<User?>> = callbackFlow {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { createUserTask ->
                // Send validation email
                if (createUserTask.isSuccessful) {
                    val user = createUserTask.result.user
                    user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            launch {
                                send(Result.success(user.toDomainUserModel()))
                            }
                        } else {
                            launch { send(Result.failure(verificationTask.exception as Throwable)) }
                        }
                    }
                } else {
                    launch { send(Result.failure(createUserTask.exception as Throwable)) }
                }
            }
            .addOnFailureListener {
                launch { send(Result.failure(it)) }
            }
        awaitClose()
    }

    override suspend fun verifyEmailIsVerified(): Boolean {
        firebaseAuth.currentUser?.reload()?.await()
        return firebaseAuth.currentUser?.isEmailVerified == true
    }

    override fun logOut() = firebaseAuth.signOut()

    override fun deleteAccountWithReAuthentication(email: String, password: String): Flow<Result<Boolean>> =
        callbackFlow {
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

    override fun deleteGoogleAccount(): Flow<Result<Boolean>> = callbackFlow {
        firebaseAuth.currentUser?.delete()
            ?.addOnSuccessListener {
                launch { send(Result.success(true)) }
            }
            ?.addOnFailureListener { exception ->
                launch { send(Result.failure(exception)) }
            }
        awaitClose()
    }

    override fun loginWithGoogle(idToken: String): Flow<Result<User>> = callbackFlow {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                authResult.user.let { firebaseUser ->
                    if (firebaseUser != null) {
                        launch { send(Result.success(firebaseUser.toDomainUserModel())) }
                    } else {
                        launch { send(Result.failure(Throwable("User not found"))) }
                    }
                }
            }
            .addOnFailureListener {
                launch { send(Result.failure(it)) }
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

    override fun getProvider(): Flow<String?> = callbackFlow {
        val user = firebaseAuth.currentUser
        if (user != null) {
            // Check if the user is logged with google
            if (user.providerData.any { it.providerId.contains(GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD) }) {
                launch { send(GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD) }
            } else if (user.providerData.any {
                    it.providerId.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)
                }
            ) {
                launch { send(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD) }
            } else {
                launch { send(null) }
            }
        } else {
            launch { send(null) }
        }
        awaitClose()
    }
}

interface FirebaseAuthRepository {
    fun getUser(): Flow<FirebaseUser?>
    fun login(email: String, password: String): Flow<Result<User?>>
    fun register(email: String, password: String): Flow<Result<User?>>
    suspend fun verifyEmailIsVerified(): Boolean
    fun loginWithGoogle(idToken: String): Flow<Result<User>>
    fun logOut()
    fun deleteAccountWithReAuthentication(email: String, password: String): Flow<Result<Boolean>>
    fun deleteGoogleAccount(): Flow<Result<Boolean>>
    fun recoverPassword(email: String): Flow<Result<Boolean>>
    fun isTokenValid(): Flow<Boolean>
    fun getProvider(): Flow<String?>
}
