package com.digitalsolution.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class UpdateGroupRemote(

    @SerializedName("id")
    val groupID: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("user_id")
    val userId: Int? = null,
)
