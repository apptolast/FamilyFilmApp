package com.apptolast.familyfilmapp.model.local

import com.apptolast.familyfilmapp.model.local.types.MovieStatus
import com.google.firebase.auth.FirebaseUser
import java.util.Locale

data class User(
    val uid: String,
    val email: String,
    val userName: String,
    val language: String,
    val statusMovies: Map<String, MovieStatus>, // Map with key-value pair: MovieId, Status
) {
    constructor() : this(
        uid = "",
        email = "",
        userName = "",
        language = "",
        statusMovies = mapOf(),
    )
}

fun FirebaseUser.toDomainUserModel(): User = User(
    uid = this.uid,
    email = this.email ?: "email not found",
    userName = this.displayName ?: "display name not found",
    language = Locale.getDefault().toLanguageTag(),
    statusMovies = mapOf(),
)
