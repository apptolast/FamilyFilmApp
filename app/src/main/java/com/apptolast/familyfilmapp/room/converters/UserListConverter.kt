package com.apptolast.familyfilmapp.room.converters

import androidx.room.TypeConverter
import com.apptolast.familyfilmapp.model.room.UserTable
import com.google.gson.Gson

class UserListConverter {

    @TypeConverter
    fun userListToJson(users: List<UserTable>): String = Gson().toJson(users)

    @TypeConverter
    fun toUserList(usersString: String): List<UserTable> =
        Gson().fromJson(usersString, Array<UserTable>::class.java).toList()
}
