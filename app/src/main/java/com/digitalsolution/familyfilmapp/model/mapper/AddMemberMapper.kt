package com.digitalsolution.familyfilmapp.model.mapper

import com.digitalsolution.familyfilmapp.model.local.AddMemberGroup
import com.digitalsolution.familyfilmapp.model.remote.request.AddMemberBody
import com.digitalsolution.familyfilmapp.model.remote.response.AddMemberRemote

object AddMemberMapper {
    fun String.toAddMemberBody() = AddMemberBody(
        email = this,
    )

    fun AddMemberRemote.toDomain() = AddMemberGroup(
        userId = userId ?: -1,
        groupId = groupId ?: -1,
    )
}
