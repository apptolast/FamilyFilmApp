package com.apptolast.familyfilmapp.model.mapper

import com.apptolast.familyfilmapp.model.local.AddGroup
import com.apptolast.familyfilmapp.model.remote.request.AddGroupBody
import com.apptolast.familyfilmapp.model.remote.response.AddGroupRemote

object AddGroupsMapper {
    fun String.toBody() = AddGroupBody(
        name = this,
    )

    fun AddGroupRemote.toDomain() = AddGroup(
        id = id ?: -1,
        name = name ?: "",
    )
}
