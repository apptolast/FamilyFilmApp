package com.apptolast.familyfilmapp.room.converters

import androidx.room.TypeConverter
import com.apptolast.familyfilmapp.model.local.types.MovieStatus

class MovieStatusConverter {
    @TypeConverter
    fun fromMovieStatus(status: MovieStatus): String = status.name

    @TypeConverter
    fun toMovieStatus(value: String): MovieStatus = MovieStatus.valueOf(value)
}
