package com.apptolast.familyfilmapp.model.local

data class User(
    val id: String,
    val email: String,
    val language: String,
    val photoUrl: String,
    val username: String? = null,
    val hasRemovedAds: Boolean = false,
) {
    constructor() : this(
        id = "",
        email = "",
        language = "",
        photoUrl = "",
        username = null,
        hasRemovedAds = false,
    )

    val displayName: String get() = username?.takeIf { it.isNotBlank() } ?: email
}
