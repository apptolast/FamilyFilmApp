package com.apptolast.familyfilmapp.model.remote.request

import com.google.gson.annotations.SerializedName

data class LoginBody(
    @SerializedName("email") val name: String,
    @SerializedName("password") val password: String,
    @SerializedName("language") val language: String = "es",
)
