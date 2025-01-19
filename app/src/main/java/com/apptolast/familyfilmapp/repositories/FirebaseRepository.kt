package com.apptolast.familyfilmapp.repositories

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

class FirebaseRepositoryImpl @Inject constructor(private val firebaseAuth: FirebaseAuth) : FirebaseRepository {

    override val userState: StateFlow<Result<FirebaseUser?>>
        field: MutableStateFlow<Result<FirebaseUser?>> = MutableStateFlow(Result.success(null))

    override fun login(email: String, password: String) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    userState.value = Result.success(firebaseAuth.currentUser)
                }
                .addOnFailureListener {
                    userState.value = Result.success(null)
                }
        } catch (e: Exception) {
            userState.value = Result.failure(e)
        }
    }

//    override fun login(email: String, password: String): Flow<FirebaseUser?> = channelFlow {
//        firebaseAuth.signInWithEmailAndPassword(email, password)
//            .addOnSuccessListener {
//                launch {
//                    send(firebaseAuth.currentUser)
//                }
//            }
//            .addOnFailureListener {
//                throw it
//            }
//        awaitClose()
//    }

    override fun register(email: String, password: String): Flow<FirebaseUser?> = channelFlow {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                launch {
                    send(firebaseAuth.currentUser)
                }
            }
            .addOnFailureListener {
                throw it
            }
        awaitClose()
    }

    override fun loginWithGoogle(idToken: String): Flow<Result<AuthResult>> = channelFlow {
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

    override fun getUser(): Flow<FirebaseUser?> = channelFlow {
        firebaseAuth.addAuthStateListener {
            launch {
                send(it.currentUser)
            }
        }
        awaitClose()
    }

    override fun recoverPassword(email: String): Flow<Result<Boolean>> = channelFlow {
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
}

interface FirebaseRepository {
    val userState: StateFlow<Result<FirebaseUser?>>

    fun login(email: String, password: String)
    //    fun login(email: String, password: String): Flow<FirebaseUser?>

    fun register(email: String, password: String): Flow<FirebaseUser?>
    fun loginWithGoogle(idToken: String): Flow<Result<AuthResult>>
    fun getUser(): Flow<FirebaseUser?>
    fun recoverPassword(email: String): Flow<Result<Boolean>>
}
