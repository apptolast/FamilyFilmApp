package com.apptolast.familyfilmapp.repositories

import com.apptolast.familyfilmapp.auth.GoogleSignInTokens
import com.apptolast.familyfilmapp.firebase.toDomainUserModel
import com.apptolast.familyfilmapp.model.local.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface FirebaseAuthRepository {
    fun checkEmailVerification(intervalMillis: Long = 5_000): Flow<Boolean>
    fun getUser(): Flow<FirebaseUser?>
    fun login(email: String, password: String): Flow<Result<User?>>
    fun register(email: String, password: String): Flow<Result<User?>>
    suspend fun verifyEmailIsVerified(): Boolean
    fun loginWithGoogle(tokens: GoogleSignInTokens): Flow<Result<User>>
    suspend fun logOut()
    fun deleteAccountWithReAuthentication(email: String, password: String): Flow<Result<Boolean>>
    fun deleteGoogleAccount(): Flow<Result<Boolean>>
    fun deleteAppleAccount(): Flow<Result<Boolean>>
    fun recoverPassword(email: String): Flow<Result<Boolean>>
    fun isTokenValid(): Flow<Boolean>
    fun getProvider(): Flow<String?>
}

class FirebaseAuthRepositoryImpl : FirebaseAuthRepository {

    private val firebaseAuth get() = Firebase.auth

    override fun checkEmailVerification(intervalMillis: Long): Flow<Boolean> = flow {
        var verified = false
        while (!verified) {
            verified = runCatching { verifyEmailIsVerified() }.getOrDefault(false)
            emit(verified)
            if (verified) break
            delay(intervalMillis)
        }
    }

    override fun getUser(): Flow<FirebaseUser?> = firebaseAuth.authStateChanged

    override fun login(email: String, password: String): Flow<Result<User?>> = flow {
        emit(
            runCatching {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password)
                val user = result.user ?: error("No user returned by signInWithEmailAndPassword")
                if (user.isEmailVerified) user.toDomainUserModel() else error("Email not verified")
            },
        )
    }

    override fun register(email: String, password: String): Flow<Result<User?>> = flow {
        emit(
            runCatching {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
                val user = result.user ?: error("No user returned by createUserWithEmailAndPassword")
                user.sendEmailVerification()
                user.toDomainUserModel()
            },
        )
    }

    override suspend fun verifyEmailIsVerified(): Boolean {
        val user = firebaseAuth.currentUser ?: return false
        user.reload()
        return user.isEmailVerified
    }

    override suspend fun logOut() {
        // The auth state listener (authStateChanged) reacts to this and clears the UI.
        firebaseAuth.signOut()
    }

    override fun deleteAccountWithReAuthentication(email: String, password: String): Flow<Result<Boolean>> = flow {
        emit(
            runCatching {
                val user = firebaseAuth.currentUser ?: error("No user logged in")
                val credential = EmailAuthProvider.credential(email, password)
                user.reauthenticate(credential)
                user.delete()
                true
            },
        )
    }

    override fun deleteGoogleAccount(): Flow<Result<Boolean>> = flow {
        emit(
            runCatching {
                val user = firebaseAuth.currentUser ?: error("No user logged in")
                user.delete()
                true
            },
        )
    }

    // Token revocation (https://appleid.apple.com/auth/revoke) is required by
    // App Store guideline 5.1.1(v) and can only be performed server-side because
    // it needs the Sign in with Apple private key. That should live in a Cloud
    // Function; here we only delete the Firebase user.
    override fun deleteAppleAccount(): Flow<Result<Boolean>> = flow {
        emit(
            runCatching {
                val user = firebaseAuth.currentUser ?: error("No user logged in")
                user.delete()
                true
            },
        )
    }

    override fun loginWithGoogle(tokens: GoogleSignInTokens): Flow<Result<User>> = flow {
        emit(
            runCatching {
                val credential = GoogleAuthProvider.credential(tokens.idToken, tokens.accessToken)
                val result = firebaseAuth.signInWithCredential(credential)
                val user = result.user ?: error("No user returned by signInWithCredential")
                user.toDomainUserModel()
            },
        )
    }

    override fun recoverPassword(email: String): Flow<Result<Boolean>> = flow {
        emit(
            runCatching {
                firebaseAuth.sendPasswordResetEmail(email)
                true
            },
        )
    }

    override fun isTokenValid(): Flow<Boolean> = flow {
        emit(
            runCatching {
                val user = firebaseAuth.currentUser ?: return@runCatching false
                user.getIdToken(forceRefresh = true)
                true
            }.getOrDefault(false),
        )
    }

    override fun getProvider(): Flow<String?> = flow {
        val user = firebaseAuth.currentUser
        val provider = user?.providerData?.firstOrNull { it.providerId != "firebase" }?.providerId
        emit(provider)
    }
}
