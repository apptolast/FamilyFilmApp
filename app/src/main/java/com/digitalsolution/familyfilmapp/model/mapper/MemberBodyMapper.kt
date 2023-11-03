package com.digitalsolution.familyfilmapp.model.mapper

import com.digitalsolution.familyfilmapp.model.remote.request.AddMemberBody

object MemberBodyMapper {
    fun String.toBody() = AddMemberBody(
        email = this,
    )
}
