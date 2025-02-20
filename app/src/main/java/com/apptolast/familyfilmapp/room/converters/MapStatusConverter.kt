package com.apptolast.familyfilmapp.room.converters

import androidx.room.TypeConverter
import com.apptolast.familyfilmapp.ui.screens.detail.MovieStatus
import com.compose.type_safe_args.annotation.gson
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class MapStatusConverter {

    @TypeConverter
    fun fromMovieStatusMap(value: Map<String, MovieStatus>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toMovieStatusMap(value: String): Map<String, MovieStatus> {
        val mapType = object : TypeToken<Map<String, MovieStatus>>() {}.type
        return Gson().fromJson(value, mapType)
    }
}
