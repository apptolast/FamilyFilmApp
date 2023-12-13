package com.apptolast.familyfilmapp.model.mapper

import com.apptolast.familyfilmapp.model.local.GenreInfo
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.mapper.MovieMapper.toDomain
import com.apptolast.familyfilmapp.model.remote.response.GenreInfoRemote

object GenreMapper {

    fun GenreInfoRemote.toDomain() = GenreInfo(
        id = id ?: -1,
        name = genreName ?: "",
        movies = movies?.map {
            it.movie?.toDomain() ?: Movie()
        } ?: emptyList(),
    )
}
