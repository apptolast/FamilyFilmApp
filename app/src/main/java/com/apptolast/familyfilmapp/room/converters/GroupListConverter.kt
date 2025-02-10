package com.apptolast.familyfilmapp.room.converters

import androidx.room.TypeConverter
import com.apptolast.familyfilmapp.model.room.GroupTable
import com.google.gson.Gson

class GroupListConverter {

    @TypeConverter
    fun groupListToJson(groups: List<GroupTable>): String = Gson().toJson(groups)

    @TypeConverter
    fun toGroupList(groupsString: String): List<GroupTable> =
        Gson().fromJson(groupsString, Array<GroupTable>::class.java).toList()
}
