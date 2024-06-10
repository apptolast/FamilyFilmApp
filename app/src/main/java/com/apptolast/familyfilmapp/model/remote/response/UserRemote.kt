package com.apptolast.familyfilmapp.model.remote.response

import com.apptolast.familyfilmapp.model.local.User
import com.google.gson.annotations.SerializedName

data class UserRemote(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("language")
    val language: String? = null,

    @SerializedName("role")
    val provider: String? = null,
)

fun UserRemote.toDomain() = User(
    id = id ?: -1,
    email = email ?: "",
    language = language ?: "es",
    provider = "",
)
