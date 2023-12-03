package com.digitalsolution.familyfilmapp.model.mapper

import com.digitalsolution.familyfilmapp.model.local.UserInfoGroup
import com.digitalsolution.familyfilmapp.model.remote.response.UserRemote
import com.digitalsolution.familyfilmapp.model.remote.response.UsersRemote

object UserMapper {


    fun UserRemote.toDomain() = UserInfoGroup(
        id = userId ?: -1,
        email = email ?: "",
        firebaseUUID = firebaseUuid ?: "",
        role = role ?: "",
    )
}
