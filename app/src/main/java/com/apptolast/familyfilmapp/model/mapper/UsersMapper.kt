package com.apptolast.familyfilmapp.model.mapper

import com.apptolast.familyfilmapp.model.local.UserInfoGroup
import com.apptolast.familyfilmapp.model.local.Users
import com.apptolast.familyfilmapp.model.mapper.UserMapper.toDomain
import com.apptolast.familyfilmapp.model.remote.response.UsersRemote

object UsersMapper {

    fun UsersRemote.toDomain() = Users(
        userID = userId ?: -1,
        groupID = groupId ?: -1,
        user = user?.toDomain() ?: UserInfoGroup(),
    )
}
