package com.digitalsolution.familyfilmapp.model.mapper

import com.digitalsolution.familyfilmapp.model.local.UserInfoGroup
import com.digitalsolution.familyfilmapp.model.local.Users
import com.digitalsolution.familyfilmapp.model.mapper.UserMapper.toDomain
import com.digitalsolution.familyfilmapp.model.remote.response.UsersRemote

object UsersMapper {


    fun UsersRemote.toDomain() = Users(
        userID = userId ?: -1,
        groupID = groupId ?: -1,
        user = user?.toDomain() ?: UserInfoGroup(),
    )


}
