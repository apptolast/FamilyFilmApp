package com.apptolast.familyfilmapp.model.remote.response

import com.apptolast.familyfilmapp.model.local.Users
import com.apptolast.familyfilmapp.model.local.toRoleType
import com.google.gson.annotations.SerializedName

data class UsersRemote(

    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("firebase_uuid")
    val firebaseUuid: String? = null,

    @SerializedName("user")
    val role: String? = null,
)

fun UsersRemote.toDomain() = Users(
    userId = userId ?: -1,
    email = email ?: "",
    firebaseUuid = firebaseUuid ?: "",
    role = role.toRoleType(),
)
