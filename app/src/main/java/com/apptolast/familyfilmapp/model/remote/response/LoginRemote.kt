package com.apptolast.familyfilmapp.model.remote.response

import com.apptolast.familyfilmapp.model.local.LoginInfo
import com.google.gson.annotations.SerializedName

data class LoginRemote(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("token_type") val tokenType: String?,
)

fun LoginRemote.toDomain() = LoginInfo(
    accessToken = accessToken ?: "",
    tokenType = tokenType ?: "",
)
