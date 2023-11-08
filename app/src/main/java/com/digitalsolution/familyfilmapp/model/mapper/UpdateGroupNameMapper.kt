package com.digitalsolution.familyfilmapp.model.mapper

import com.digitalsolution.familyfilmapp.model.local.UpdateGroupName
import com.digitalsolution.familyfilmapp.model.remote.request.UpdateGroupNameBody
import com.digitalsolution.familyfilmapp.model.remote.response.UpdateGroupRemote

object UpdateGroupNameMapper {
    fun String.toBodyUpdateGroup() = UpdateGroupNameBody(
        name = this,
    )

    fun UpdateGroupRemote.toDomain() = UpdateGroupName(
        id = groupID ?: -1,
        name = name ?: "",
        userId = userId ?: -1,
    )
}
