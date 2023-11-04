package com.digitalsolution.familyfilmapp.model.mapper

import com.digitalsolution.familyfilmapp.model.remote.body.GroupBody

object AddGroupsMapper {
    fun String.toBody() = GroupBody(
        name = this,
    )
}
