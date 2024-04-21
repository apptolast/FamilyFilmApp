package com.apptolast.familyfilmapp.model.remote.response

import com.apptolast.familyfilmapp.model.local.UserInfoGroup
import com.apptolast.familyfilmapp.model.local.Users
import com.google.gson.annotations.SerializedName

data class UsersRemote(

    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("group_id")
    val groupId: Int? = null,

    @SerializedName("user")
    val user: UserRemote? = null,

)

fun UsersRemote.toDomain() = Users(
    userID = userId ?: -1,
    groupID = groupId ?: -1,
    user = user?.toDomain() ?: UserInfoGroup(),
)
