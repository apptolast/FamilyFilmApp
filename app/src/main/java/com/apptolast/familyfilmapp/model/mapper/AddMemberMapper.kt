package com.apptolast.familyfilmapp.model.mapper

import com.apptolast.familyfilmapp.model.local.AddMemberGroup
import com.apptolast.familyfilmapp.model.remote.request.AddMemberBody
import com.apptolast.familyfilmapp.model.remote.response.AddMemberRemote

object AddMemberMapper {
    fun String.toAddMemberBody() = AddMemberBody(
        email = this,
    )

    fun AddMemberRemote.toDomain() = AddMemberGroup(
        userId = userId ?: -1,
        groupId = groupId ?: -1,
    )
}
