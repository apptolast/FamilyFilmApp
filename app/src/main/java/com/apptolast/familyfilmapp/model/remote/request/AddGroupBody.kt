package com.apptolast.familyfilmapp.model.remote.request

import com.google.gson.annotations.SerializedName

data class AddGroupBody(
    @SerializedName("name") val name: String,
)
