package com.apptolast.familyfilmapp.model.remote.response

import com.apptolast.familyfilmapp.model.local.User
import com.google.gson.annotations.SerializedName

data class UserResponseRemote(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("language")
    val language: String? = null,

    @SerializedName("provider")
    val provider: String? = null,

    )

fun UserResponseRemote.toDomain() = User(
    id = id ?: -1,
    email = email ?: "",
    language = language ?: "es",
    provider = "",
)
