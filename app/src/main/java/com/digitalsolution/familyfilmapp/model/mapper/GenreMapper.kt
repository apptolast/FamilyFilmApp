package com.digitalsolution.familyfilmapp.model.mapper

import com.digitalsolution.familyfilmapp.model.local.GenreInfo
import com.digitalsolution.familyfilmapp.model.local.Movie
import com.digitalsolution.familyfilmapp.model.mapper.MovieMapper.toDomain
import com.digitalsolution.familyfilmapp.model.remote.response.GenreInfoRemote

object GenreMapper {

    fun GenreInfoRemote.toDomain() = GenreInfo(
        id = id ?: -1,
        name = genreName ?: "",
        movies = movies?.flatMap { movieWrapper ->
            movieWrapper.movie?.map { movieInfoRemote ->
                movieInfoRemote.movieRemote?.toDomain() ?: Movie()
            } ?: emptyList()
        } ?: emptyList(),
    )
}
