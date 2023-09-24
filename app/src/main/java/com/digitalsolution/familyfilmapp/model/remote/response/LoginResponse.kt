package com.digitalsolution.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("token")
    val token: String? = null
)
