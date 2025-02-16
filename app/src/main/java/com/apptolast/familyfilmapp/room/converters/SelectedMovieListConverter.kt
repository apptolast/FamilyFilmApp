package com.apptolast.familyfilmapp.room.converters

import androidx.room.TypeConverter
import com.apptolast.familyfilmapp.model.local.SelectedMovie
import com.google.gson.Gson

class SelectedMovieListConverter {

    @TypeConverter
    fun selectedMovieListToJson(selectedMovieList: List<SelectedMovie>): String = Gson().toJson(selectedMovieList)

    @TypeConverter
    fun toSelectedMovieList(selectedMovieListString: String): List<SelectedMovie> =
        Gson().fromJson(selectedMovieListString, Array<SelectedMovie>::class.java).toList()
}
