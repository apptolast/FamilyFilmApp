package com.apptolast.familyfilmapp.repositories

import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieRemote
import com.apptolast.familyfilmapp.network.TmdbApi
import javax.inject.Inject

class TmdbDatasourceImpl @Inject constructor(
    private val tmdbApi: TmdbApi,
) : TmdbDatasource {

    override suspend fun getPopularMovies(): List<TmdbMovieRemote> =
        tmdbApi.getPopularMovies().results

}

interface TmdbDatasource {

    suspend fun getPopularMovies(): List<TmdbMovieRemote>
}
