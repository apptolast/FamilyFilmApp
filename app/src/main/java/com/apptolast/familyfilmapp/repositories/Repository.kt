package com.apptolast.familyfilmapp.repositories

import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.toDomain
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
//    private val roomDatasource: RoomDatasource,
//    private val firebaseDatasource: FirebaseDatasource,
    private val tmdbDatasource: TmdbDatasource,
) : Repository {

    override suspend fun getPopularMovies(): List<Movie> =
        tmdbDatasource.getPopularMovies().map { it.toDomain() }

}


interface Repository {

    suspend fun getPopularMovies(): List<Movie>

}
