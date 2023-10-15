package com.digitalsolution.familyfilmapp.model.remote.request

import com.google.gson.annotations.SerializedName

data class RegisterBody(
    @SerializedName("email") val email: String,
    @SerializedName("firebase_uuid") val firebaseId: String,
)
