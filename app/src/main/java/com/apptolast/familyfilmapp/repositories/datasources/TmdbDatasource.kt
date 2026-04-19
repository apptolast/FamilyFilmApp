package com.apptolast.familyfilmapp.repositories.datasources

import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMultiSearchResultRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbTvShowRemote
import com.apptolast.familyfilmapp.network.TmdbApi
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import javax.inject.Inject

class TmdbDatasourceImpl @Inject constructor(
    private val tmdbApi: TmdbApi,
    private val tmdbLocaleManager: TmdbLocaleManager,
) : TmdbDatasource {

    override suspend fun getPopularMovies(page: Int): List<TmdbMovieRemote> =
        tmdbApi.getPopularMovies(page).results.filterAdult()

    override suspend fun searchMovieByName(string: String): List<TmdbMovieRemote> {
        val includeAdult = tmdbLocaleManager.includeAdult.value
        return tmdbApi.searchMovieByName(string, adult = includeAdult).results.filterAdult()
    }

    override suspend fun searchMovieById(movieId: Int): TmdbMovieRemote = tmdbApi.searchMovieById(movieId)

    override suspend fun getPopularTvShows(page: Int) = tmdbApi.getPopularTvShows(page).results.filterAdult()

    override suspend fun searchMulti(query: String): List<TmdbMultiSearchResultRemote> {
        val includeAdult = tmdbLocaleManager.includeAdult.value
        return tmdbApi.searchMulti(query, adult = includeAdult).results.filterAdult()
    }

    private fun <T : Any> List<T>.filterAdult(): List<T> {
        if (tmdbLocaleManager.includeAdult.value) return this
        return filter { item ->
            when (item) {
                is TmdbMovieRemote -> !item.adult
                is TmdbTvShowRemote -> !item.adult
                is TmdbMultiSearchResultRemote -> !item.adult
                else -> true
            }
        }
    }

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
