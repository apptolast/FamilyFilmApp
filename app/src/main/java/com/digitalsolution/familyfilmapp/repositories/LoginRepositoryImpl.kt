package com.digitalsolution.familyfilmapp.repositories

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : LoginRepository {

    override fun loginEmailPass(email: String, password: String): Flow<Result<AuthResult>> =
        channelFlow {
            firebaseAuth.signInWithEmailAndPassword(email, password)
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

    override fun register(email: String, password: String): Flow<Result<AuthResult>> =
        channelFlow {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
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

    override fun loginWithGoogle(idToken: String): Flow<Result<AuthResult>> =
        channelFlow {
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

    override fun getUser() = firebaseAuth.currentUser
}

interface LoginRepository {
    fun loginEmailPass(email: String, password: String): Flow<Result<AuthResult>>
    fun register(email: String, password: String): Flow<Result<AuthResult>>
    fun loginWithGoogle(idToken: String): Flow<Result<AuthResult>>
    fun getUser(): FirebaseUser?
}
