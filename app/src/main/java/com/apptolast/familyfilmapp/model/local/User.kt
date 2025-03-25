package com.apptolast.familyfilmapp.model.local

import com.apptolast.familyfilmapp.model.local.types.MovieStatus
import com.google.firebase.auth.FirebaseUser
import java.util.Locale

data class User(
    val id: String,
    val email: String,
    val language: String,
    val statusMovies: Map<String, MovieStatus>, // Map with key-value pair: MovieId, Status
) {
    constructor() : this(
        id = "",
        email = "",
        language = "",
        statusMovies = mapOf(),
    )
}

fun FirebaseUser.toDomainUserModel(): User = User(
    id = this.uid,
    email = this.email ?: "email not found",
    language = Locale.getDefault().toLanguageTag(),
    statusMovies = mapOf(),
)
