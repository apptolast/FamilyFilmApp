package com.digitalsolution.familyfilmapp.model.mapper

import com.digitalsolution.familyfilmapp.extensions.toDate
import com.digitalsolution.familyfilmapp.model.local.Genre
import com.digitalsolution.familyfilmapp.model.local.Movie
import com.digitalsolution.familyfilmapp.model.remote.response.GenresRemote
import com.digitalsolution.familyfilmapp.model.remote.response.MovieRemote
import java.util.Calendar

object MovieMapper {

    fun MovieRemote.toDomain() = Movie(
        title = title ?: "",
        isAdult = adult ?: true,
        genres = genre?.map { it.toDomain() } ?: emptyList(),
        image = image ?: "",
        synopsis = synopsis ?: "",
        voteAverage = voteAverage ?: 0f,
        voteCount = voteCount ?: 0,
        releaseDate = releaseDate?.toDate() ?: Calendar.getInstance().time,
        language = language ?: "",
    )

    private fun GenresRemote.toDomain() = Genre(
        movieId = movieId ?: -1,
        genreId = genreId ?: -1,
    )
}
