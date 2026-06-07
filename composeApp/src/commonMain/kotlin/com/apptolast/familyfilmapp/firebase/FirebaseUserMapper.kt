package com.apptolast.familyfilmapp.firebase

import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.network.systemLanguageTag
import dev.gitlive.firebase.auth.FirebaseUser

fun FirebaseUser.toDomainUserModel(): User = User(
    id = uid,
    email = email ?: "email not found",
    language = systemLanguageTag(),
    photoUrl = photoURL.orEmpty(),
)
