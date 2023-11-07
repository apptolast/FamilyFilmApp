package com.digitalsolution.familyfilmapp.model.mapper

import com.digitalsolution.familyfilmapp.model.local.AddGroup
import com.digitalsolution.familyfilmapp.model.remote.request.AddGroupBody
import com.digitalsolution.familyfilmapp.model.remote.response.AddGroupRemote

object AddGroupsMapper {
    fun String.toBody() = AddGroupBody(
        name = this,
    )

    fun AddGroupRemote.toDomain() = AddGroup(
        id = id ?: -1,
        name = name ?: "",
        userId = userId ?: -1,
    )
}
