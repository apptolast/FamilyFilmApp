package com.apptolast.familyfilmapp.room.converters

import androidx.room.TypeConverter
import com.google.gson.Gson

class StringListConverter {

    @TypeConverter
    fun intListToJson(stringList: List<String>): String = Gson().toJson(stringList)

    @TypeConverter
    fun toIntList(stringListString: String): List<String> =
        Gson().fromJson(stringListString, Array<String>::class.java).toList()
}
