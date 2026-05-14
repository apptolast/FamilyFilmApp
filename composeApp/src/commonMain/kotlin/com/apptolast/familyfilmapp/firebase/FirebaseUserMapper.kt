package com.apptolast.familyfilmapp.firebase

import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.network.systemLanguageTag
import dev.gitlive.firebase.auth.FirebaseUser

/**
 * Mapper from the GitLive multiplatform [FirebaseUser] to the domain [User].
 *
 * Replaces the legacy `FirebaseUser.toDomainUserModel()` extension that used
 * the Android Firebase Auth SDK + `java.util.Locale.getDefault().toLanguageTag()`.
 * The language tag now comes from the multiplatform `systemLanguageTag()` helper.
 */
fun FirebaseUser.toDomainUserModel(): User = User(
    id = uid,
    email = email ?: "email not found",
    language = systemLanguageTag(),
    photoUrl = photoURL.orEmpty(),
)
