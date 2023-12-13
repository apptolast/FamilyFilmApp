package com.apptolast.familyfilmapp.model.remote.request

import com.google.gson.annotations.SerializedName

data class RemoveMemberBody(
    @SerializedName("userId") val userId: Int,
)
