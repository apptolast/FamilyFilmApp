package com.apptolast.familyfilmapp.room.converters

import androidx.room.TypeConverter
import com.apptolast.familyfilmapp.model.room.UserTable
import com.google.gson.Gson

class UserListConverter {


    @TypeConverter
    fun userListToJson(users: List<UserTable>): String {
        return Gson().toJson(users)
    }

    @TypeConverter
    fun toUserList(usersString: String): List<UserTable> {
        return Gson().fromJson(usersString, Array<UserTable>::class.java).toList()
    }
}
