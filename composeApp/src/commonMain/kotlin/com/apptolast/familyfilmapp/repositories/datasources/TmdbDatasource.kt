package com.apptolast.familyfilmapp.repositories.datasources

import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMultiSearchResultRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbTvShowRemote
import com.apptolast.familyfilmapp.network.TmdbApi
import com.apptolast.familyfilmapp.network.TmdbLocaleManager

class TmdbDatasourceImpl(private val tmdbApi: TmdbApi, private val tmdbLocaleManager: TmdbLocaleManager) :
    TmdbDatasource {

    override suspend fun getPopularMovies(page: Int): List<TmdbMovieRemote> =
        tmdbApi.getPopularMovies(page = page, language = currentLanguage()).results.filterAdult()

    override suspend fun searchMovieByName(string: String): List<TmdbMovieRemote> = tmdbApi.searchMovieByName(
        query = string,
        includeAdult = tmdbLocaleManager.includeAdult.value,
        language = currentLanguage(),
    ).results.filterAdult()

    override suspend fun searchMovieById(movieId: Int): TmdbMovieRemote =
        tmdbApi.searchMovieById(movieId = movieId, language = currentLanguage())

    override suspend fun getPopularTvShows(page: Int): List<TmdbTvShowRemote> =
        tmdbApi.getPopularTvShows(page = page, language = currentLanguage()).results.filterAdult()

    override suspend fun searchMulti(query: String): List<TmdbMultiSearchResultRemote> = tmdbApi.searchMulti(
        query = query,
        includeAdult = tmdbLocaleManager.includeAdult.value,
        language = currentLanguage(),
    ).results.filterAdult()

    override suspend fun getTvShowById(tvId: Int): TmdbTvShowRemote =
        tmdbApi.getTvShowById(tvId = tvId, language = currentLanguage())

    private fun currentLanguage(): String = tmdbLocaleManager.languageTag.value

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
}

interface TmdbDatasource {
    suspend fun getPopularMovies(page: Int = 1): List<TmdbMovieRemote>
    suspend fun searchMovieByName(string: String): List<TmdbMovieRemote>
    suspend fun searchMovieById(movieId: Int): TmdbMovieRemote
    suspend fun getPopularTvShows(page: Int = 1): List<TmdbTvShowRemote>
    suspend fun searchMulti(query: String): List<TmdbMultiSearchResultRemote>
    suspend fun getTvShowById(tvId: Int): TmdbTvShowRemote
}
