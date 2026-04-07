package com.apptolast.familyfilmapp.repositories.datasources

import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMultiSearchResultRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbTvShowRemote
import com.apptolast.familyfilmapp.network.TmdbApi
import javax.inject.Inject

class TmdbDatasourceImpl @Inject constructor(private val tmdbApi: TmdbApi) : TmdbDatasource {

    override suspend fun getPopularMovies(page: Int): List<TmdbMovieRemote> = tmdbApi.getPopularMovies(page).results

    override suspend fun searchMovieByName(string: String): List<TmdbMovieRemote> =
        tmdbApi.searchMovieByName(string).results

    override suspend fun searchMovieById(movieId: Int): TmdbMovieRemote = tmdbApi.searchMovieById(movieId)

    override suspend fun getPopularTvShows(page: Int) = tmdbApi.getPopularTvShows(page).results

    override suspend fun searchMulti(query: String) = tmdbApi.searchMulti(query).results

    override suspend fun getTvShowById(tvId: Int) = tmdbApi.getTvShowById(tvId)
}

interface TmdbDatasource {
    suspend fun getPopularMovies(page: Int = 1): List<TmdbMovieRemote>
    suspend fun searchMovieByName(string: String): List<TmdbMovieRemote>
    suspend fun searchMovieById(movieId: Int): TmdbMovieRemote
    suspend fun getPopularTvShows(page: Int = 1): List<TmdbTvShowRemote>
    suspend fun searchMulti(query: String): List<TmdbMultiSearchResultRemote>
    suspend fun getTvShowById(tvId: Int): TmdbTvShowRemote
}
