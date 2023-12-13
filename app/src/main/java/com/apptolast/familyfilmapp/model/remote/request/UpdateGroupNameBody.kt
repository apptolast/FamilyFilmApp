package com.apptolast.familyfilmapp.model.remote.request

import com.google.gson.annotations.SerializedName

data class UpdateGroupNameBody(
    @SerializedName("name") val name: String,
)
