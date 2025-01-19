package com.apptolast.familyfilmapp.model.remote.response

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

