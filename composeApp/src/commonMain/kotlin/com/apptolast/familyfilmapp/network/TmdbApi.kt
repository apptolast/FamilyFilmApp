package com.apptolast.familyfilmapp.network

import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieWrapperRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMultiSearchWrapperRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbTvShowRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbTvShowWrapperRemote

/**
 * TMDB v3 endpoints used by the app. Implemented by [TmdbApiKtor] in
 * production and by MockEngine-backed fakes in commonTest (block 16).
 *
 * Default values for [language] mirror the user's current locale via
 * TmdbLocaleManager; callers can pass an explicit tag to override.
 */
interface TmdbApi {

    suspend fun getPopularMovies(
        page: Int,
        language: String,
        appendProviders: Boolean = true,
    ): TmdbMovieWrapperRemote

    suspend fun searchMovieByName(
        query: String,
        includeAdult: Boolean,
        language: String,
    ): TmdbMovieWrapperRemote

    suspend fun searchMovieById(
        movieId: Int,
        language: String,
        appendProviders: Boolean = true,
    ): TmdbMovieRemote

    suspend fun getPopularTvShows(
        page: Int,
        language: String,
        appendProviders: Boolean = true,
    ): TmdbTvShowWrapperRemote

    suspend fun getTvShowById(
        tvId: Int,
        language: String,
        appendProviders: Boolean = true,
    ): TmdbTvShowRemote

    suspend fun searchMulti(
        query: String,
        includeAdult: Boolean,
        language: String,
    ): TmdbMultiSearchWrapperRemote

    companion object {
        const val PARAM_PROVIDERS_VALUE = "watch/providers"
    }
}
