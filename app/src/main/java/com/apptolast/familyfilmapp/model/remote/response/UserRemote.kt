package com.apptolast.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class UserRemote(

    @SerializedName("id")
    val userId: Int? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("firebase_uuid")
    val firebaseUuid: String? = null,

    @SerializedName("role")
    val role: String? = null,
)
