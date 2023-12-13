package com.apptolast.familyfilmapp.model.remote.request

import com.google.gson.annotations.SerializedName

data class AddMemberBody(
    @SerializedName("email") val email: String,
)
