package com.digitalsolution.familyfilmapp.repositories

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : LoginRepository {

    override fun login(email: String, password: String): Flow<Result<AuthResult>> = callbackFlow {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                trySend(Result.success(it))
                close()
            }
            .addOnFailureListener {
                trySend(Result.failure(it))
                close(it)
            }
        awaitClose()
    }

    override fun register(email: String, password: String): Flow<Result<AuthResult>> = callbackFlow {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                trySend(Result.success(it))
                close()
            }
            .addOnFailureListener {
                trySend(Result.failure(it))
                close(it)
            }
        awaitClose()
    }

}

interface LoginRepository {
    fun login(email: String, password: String): Flow<Result<AuthResult>>
    fun register(email: String, password: String): Flow<Result<AuthResult>>
}
