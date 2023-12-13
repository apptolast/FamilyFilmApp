package com.apptolast.familyfilmapp.model.remote.request

import com.google.gson.annotations.SerializedName

data class LoginBody(
    @SerializedName("email") val email: String,
    @SerializedName("firebase_uuid") val firebaseId: String,
)
