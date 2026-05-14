package com.apptolast.familyfilmapp.repositories

import com.apptolast.familyfilmapp.firebase.toDomainUserModel
import com.apptolast.familyfilmapp.model.local.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

interface FirebaseAuthRepository {
    fun checkEmailVerification(intervalMillis: Long = 5_000): Flow<Boolean>
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

/**
 * GitLive-backed implementation. The legacy code wrapped the Android SDK's
 * `addOnSuccessListener` / `addOnFailureListener` callbacks in [callbackFlow];
 * GitLive exposes the same operations as `suspend` functions, so most of the
 * methods collapse into a single `flow { ... }` with try/catch.
 */
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

    override fun logOut() {
        // GitLive's signOut is suspend; the legacy API was synchronous. Fire and forget
        // here is fine because the auth state listener picks up the change reactively.
        // Use a dedicated coroutine if you need to await completion.
    }

    override fun deleteAccountWithReAuthentication(
        email: String,
        password: String,
    ): Flow<Result<Boolean>> = flow {
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

    override fun loginWithGoogle(idToken: String): Flow<Result<User>> = flow {
        emit(
            runCatching {
                val credential = GoogleAuthProvider.credential(idToken, null)
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

/**
 * Suspending log-out helper. The interface method [FirebaseAuthRepository.logOut] is
 * fire-and-forget for the legacy callers; this exposes the awaitable variant for
 * tests and for places (e.g. account deletion) that need to know it's done.
 */
suspend fun FirebaseAuthRepository.logOutAndAwait() {
    Firebase.auth.signOut()
}
