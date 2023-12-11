package com.digitalsolution.familyfilmapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class UsersRemote(

    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("group_id")
    val groupId: Int? = null,

    @SerializedName("user")
    val user: UserRemote? = null,

)
