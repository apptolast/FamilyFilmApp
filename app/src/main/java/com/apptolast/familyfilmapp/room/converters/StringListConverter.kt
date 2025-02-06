package com.apptolast.familyfilmapp.room.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import javax.inject.Inject

class StringListConverter  {

    @Inject
    lateinit var gson: Gson

    @TypeConverter
    fun intListToJson(stringList: List<String>): String {
        return Gson().toJson(stringList)
    }

    @TypeConverter
    fun toIntList(stringListString: String): List<String> {
        return Gson().fromJson(stringListString, Array<String>::class.java).toList()
    }
}
