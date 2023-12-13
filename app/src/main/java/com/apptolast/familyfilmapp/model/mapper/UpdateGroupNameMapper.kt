package com.apptolast.familyfilmapp.model.mapper

import com.apptolast.familyfilmapp.model.local.UpdateGroupName
import com.apptolast.familyfilmapp.model.remote.request.UpdateGroupNameBody
import com.apptolast.familyfilmapp.model.remote.response.UpdateGroupRemote

object UpdateGroupNameMapper {
    fun String.toBody() = UpdateGroupNameBody(
        name = this,
    )

    fun UpdateGroupRemote.toDomain() = UpdateGroupName(
        id = groupID ?: -1,
        name = name ?: "",
        userId = userId ?: -1,
    )
}
