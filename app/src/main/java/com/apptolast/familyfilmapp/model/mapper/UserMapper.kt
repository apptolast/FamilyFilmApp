package com.apptolast.familyfilmapp.model.mapper

import com.apptolast.familyfilmapp.model.local.UserInfoGroup
import com.apptolast.familyfilmapp.model.remote.response.UserRemote

object UserMapper {

    fun UserRemote.toDomain() = UserInfoGroup(
        id = userId ?: -1,
        email = email ?: "",
        firebaseUUID = firebaseUuid ?: "",
        role = role ?: "",
    )
}
