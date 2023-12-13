package com.apptolast.familyfilmapp.model.mapper

import com.apptolast.familyfilmapp.extensions.toDate
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.remote.response.MovieRemote
import java.util.Calendar

object MovieMapper {

    fun MovieRemote.toDomain() = Movie(
        title = title ?: "",
        isAdult = adult ?: true,
        genres = genre?.map { genres ->
            genres.genre?.let { genre ->
                genre.id!! to genre.name!!
            }!!
        }!!,
        image = image ?: "",
        synopsis = synopsis ?: "",
        voteAverage = voteAverage ?: 0f,
        voteCount = voteCount ?: 0,
        releaseDate = releaseDate?.toDate() ?: Calendar.getInstance().time,
        language = language ?: "",
    )
}
