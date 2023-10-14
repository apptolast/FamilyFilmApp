package com.digitalsolution.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class ResponseWrapper<T>(
    @SerializedName("data") val data: T? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("message") val message: String? = null,
)
