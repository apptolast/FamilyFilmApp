package com.digitalsolution.familyfilmapp.repositories

import com.digitalsolution.utils.Exceptions
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : LoginRepositoryInterface {

    override suspend fun login(email: String, password: String): AuthResult? {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        } catch (error: Exceptions) {
            return null
        }
    }

}

interface LoginRepositoryInterface {
     suspend fun login(email: String, password: String): AuthResult?
}
