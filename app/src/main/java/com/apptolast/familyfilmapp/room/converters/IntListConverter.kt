package com.apptolast.familyfilmapp.room.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import javax.inject.Inject

class IntListConverter {

    @Inject
    lateinit var gson: Gson

    @TypeConverter
    fun intListToJson(intList: List<Int>): String = Gson().toJson(intList)

    @TypeConverter
    fun toIntList(intListString: String): List<Int> = Gson().fromJson(intListString, Array<Int>::class.java).toList()
}
