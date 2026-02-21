package com.apptolast.familyfilmapp.model.local

import com.google.firebase.auth.FirebaseUser
import java.util.Locale

data class User(
    val id: String,
    val email: String,
    val language: String,
    val photoUrl: String,
) {
    constructor() : this(
        id = "",
        email = "",
        language = "",
        photoUrl = "",
    )
}

fun FirebaseUser.toDomainUserModel(): User = User(
    id = this.uid,
    email = this.email ?: "email not found",
    language = Locale.getDefault().toLanguageTag(),
    photoUrl = this.photoUrl?.toString().orEmpty(),
)
