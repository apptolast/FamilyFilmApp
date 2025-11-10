package com.apptolast.familyfilmapp.repositories

import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.toDomainUserModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(private val firebaseAuth: FirebaseAuth) : FirebaseAuthRepository {

    /**
     * Starts email verification polling on-demand.
     * This method should only be called when user needs verification check (e.g., on verification screen).
     * The Flow automatically stops when:
     * - Email is verified
     * - Flow is cancelled (user leaves screen)
     * - An unrecoverable error occurs
     *
     * @param intervalMillis Polling interval in milliseconds (default 5000ms = 5 seconds)
     * @return Flow that emits verification status until verified or cancelled
     */
    override fun checkEmailVerification(intervalMillis: Long): Flow<Boolean> = callbackFlow {
        var isVerified = false

        while (!isClosedForSend && !isVerified) {
            try {
                isVerified = verifyEmailIsVerified()
                trySend(isVerified)

                if (isVerified) {
                    Timber.d("Email verified successfully!")
                    break
                }

                delay(intervalMillis)
            } catch (e: Exception) {
                Timber.e(e, "Error checking email verification")
                // Continue polling even on error (could be temporary network issue)
                delay(intervalMillis)
            }
        }

        awaitClose {
            Timber.d("Email verification check stopped")
        }
    }.flowOn(Dispatchers.IO)

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

    /**
     * Checks if the current user's email is verified.
     * Reloads user data from Firebase before checking to ensure fresh data.
     *
     * @return true if email is verified, false otherwise (including when user is null)
     * @throws Exception if network error or Firebase auth issues occur
     */
    override suspend fun verifyEmailIsVerified(): Boolean {
        return try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                Timber.w("No authenticated user found when checking email verification")
                return false
            }

            // Reload to get fresh data from Firebase
            user.reload().await()

            val isVerified = user.isEmailVerified
            Timber.d("Email verification status: $isVerified for user: ${user.email}")

            isVerified
        } catch (e: Exception) {
            Timber.e(e, "Error verifying email status")
            throw e // Re-throw to let caller handle
        }
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
    fun checkEmailVerification(intervalMillis: Long = 5000): Flow<Boolean>
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
