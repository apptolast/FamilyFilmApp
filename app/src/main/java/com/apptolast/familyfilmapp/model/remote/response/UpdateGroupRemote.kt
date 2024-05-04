package com.apptolast.familyfilmapp.model.remote.response

import com.apptolast.familyfilmapp.model.local.UpdateGroupName
import com.apptolast.familyfilmapp.model.remote.request.UpdateGroupNameBody
import com.google.gson.annotations.SerializedName

data class UpdateGroupRemote(

    @SerializedName("id")
    val groupID: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("user_id")
    val userId: Int? = null,
)

fun String.toBody() = UpdateGroupNameBody(
    name = this,
)

fun UpdateGroupRemote.toDomain() = UpdateGroupName(
    id = groupID ?: -1,
    name = name ?: "",
    userId = userId ?: -1,
)
