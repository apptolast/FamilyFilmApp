package com.apptolast.familyfilmapp.model.remote.request

import com.google.gson.annotations.SerializedName

data class LoginBody(
    @SerializedName("token") val token: String,
)
