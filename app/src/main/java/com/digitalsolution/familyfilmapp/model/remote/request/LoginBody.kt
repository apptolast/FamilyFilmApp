package com.digitalsolution.familyfilmapp.model.remote.request

import com.google.gson.annotations.SerializedName

data class LoginBody(
    @SerializedName("email") val email: String,
    @SerializedName("fbid") val fbid: String,
)