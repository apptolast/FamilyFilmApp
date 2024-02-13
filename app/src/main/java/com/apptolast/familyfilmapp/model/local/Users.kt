package com.apptolast.familyfilmapp.model.local

data class Users(
    val userID: Int,
    val groupID: Int,
    val user: UserInfoGroup,
)



val FAKE_USERS = listOf(
    Users(1, 101, FAKE_USER_INFO_GROUP[0]),
    Users(2, 102, FAKE_USER_INFO_GROUP[1]),
    Users(3, 103, FAKE_USER_INFO_GROUP[2]),
    Users(4, 104, FAKE_USER_INFO_GROUP[3]),
    Users(5, 105, FAKE_USER_INFO_GROUP[4])
)
