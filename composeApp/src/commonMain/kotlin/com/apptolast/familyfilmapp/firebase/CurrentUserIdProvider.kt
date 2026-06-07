package com.apptolast.familyfilmapp.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

// Injected into ViewModels so commonTest can supply an id without initialising Firebase.
fun interface CurrentUserIdProvider {
    fun currentUserId(): String?
}

class FirebaseCurrentUserIdProvider : CurrentUserIdProvider {
    override fun currentUserId(): String? = Firebase.auth.currentUser?.uid
}
