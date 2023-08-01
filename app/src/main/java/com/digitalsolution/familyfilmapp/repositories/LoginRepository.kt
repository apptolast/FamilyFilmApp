package com.digitalsolution.familyfilmapp.repositories

import com.digitalsolution.familyfilmapp.state.CustomResult
import com.digitalsolution.utils.Exceptions
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : LoginRepositoryInterface {

    override fun login(email: String, password: String): Flow<CustomResult> = callbackFlow {
        trySend(CustomResult.InProgress(true))
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                trySend(CustomResult.InProgress(false))
                trySend(CustomResult.Complete.Success(it))
                close()
            }
            .addOnFailureListener {
                trySend(CustomResult.InProgress(false))
                trySend(CustomResult.IsError(false))
                trySend(CustomResult.Complete.Failed(it))
                close(it)
            }
        awaitClose()
    }

    override fun register(email: String, password: String): Flow<CustomResult> = callbackFlow {
        trySend(CustomResult.InProgress(true))
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                trySend(CustomResult.InProgress(false))
                trySend(CustomResult.Complete.Success(it))
                close()
            }
            .addOnFailureListener {
                trySend(CustomResult.InProgress(false))
                trySend(CustomResult.IsError(false))
                trySend(CustomResult.Complete.Failed(it))
                close(it)
            }
        awaitClose()
    }

}

interface LoginRepositoryInterface {
     fun login(email: String, password: String): Flow<CustomResult>
     fun register(email: String, password: String): Flow<CustomResult>
}
