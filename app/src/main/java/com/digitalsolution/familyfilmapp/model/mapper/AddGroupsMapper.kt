package com.digitalsolution.familyfilmapp.model.mapper

import com.digitalsolution.familyfilmapp.model.local.GroupCreated
import com.digitalsolution.familyfilmapp.model.remote.body.GroupBody
import com.digitalsolution.familyfilmapp.model.remote.response.CreateGroupRemote

object AddGroupsMapper {
    fun String.toBody() = GroupBody(
        name = this,
    )

    fun CreateGroupRemote.toDomain() = GroupCreated(
        id = id ?: -1,
        name = name ?: "",
        userId = userId ?: -1,
    )
}
