package com.digitalsolution.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

abstract class BaseResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("message") val message: String? = null,
)
