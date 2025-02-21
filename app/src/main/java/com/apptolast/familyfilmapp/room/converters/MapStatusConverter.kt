package com.apptolast.familyfilmapp.room.converters

import androidx.room.TypeConverter
import com.apptolast.familyfilmapp.ui.screens.detail.MovieStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapStatusConverter {

    @TypeConverter
    fun fromMovieStatusMap(value: Map<String, MovieStatus>): String = Gson().toJson(value)

    @TypeConverter
    fun toMovieStatusMap(value: String): Map<String, MovieStatus> {
        val mapType = object : TypeToken<Map<String, MovieStatus>>() {}.type
        return Gson().fromJson(value, mapType)
    }
}
