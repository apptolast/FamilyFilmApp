package com.digitalsolution.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class AddMemberRemote(
    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("group_id")
    val groupId: Int? = null,
)
