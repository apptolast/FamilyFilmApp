package com.apptolast.familyfilmapp.network

import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMovieWrapperRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbMultiSearchWrapperRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbTvShowRemote
import com.apptolast.familyfilmapp.model.remote.tmdbResponse.TmdbTvShowWrapperRemote
import com.apptolast.familyfilmapp.network.TmdbApi.Companion.PARAM_PROVIDERS_VALUE
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class TmdbApiKtor(private val client: HttpClient) : TmdbApi {

    override suspend fun getPopularMovies(
        page: Int,
        language: String,
        appendProviders: Boolean,
    ): TmdbMovieWrapperRemote = client.get("movie/popular") {
        parameter(PARAM_PAGE, page)
        parameter(PARAM_LANGUAGE, language)
        if (appendProviders) parameter(PARAM_APPEND, PARAM_PROVIDERS_VALUE)
    }.body()

    override suspend fun searchMovieByName(
        query: String,
        includeAdult: Boolean,
        language: String,
    ): TmdbMovieWrapperRemote = client.get("search/movie") {
        parameter(PARAM_QUERY, query)
        parameter(PARAM_ADULT, includeAdult)
        parameter(PARAM_LANGUAGE, language)
    }.body()

    override suspend fun searchMovieById(movieId: Int, language: String, appendProviders: Boolean): TmdbMovieRemote =
        client.get("movie/$movieId") {
            parameter(PARAM_LANGUAGE, language)
            if (appendProviders) parameter(PARAM_APPEND, PARAM_PROVIDERS_VALUE)
        }.body()

    override suspend fun getPopularTvShows(
        page: Int,
        language: String,
        appendProviders: Boolean,
    ): TmdbTvShowWrapperRemote = client.get("tv/popular") {
        parameter(PARAM_PAGE, page)
        parameter(PARAM_LANGUAGE, language)
        if (appendProviders) parameter(PARAM_APPEND, PARAM_PROVIDERS_VALUE)
    }.body()

    override suspend fun getTvShowById(tvId: Int, language: String, appendProviders: Boolean): TmdbTvShowRemote =
        client.get("tv/$tvId") {
            parameter(PARAM_LANGUAGE, language)
            if (appendProviders) parameter(PARAM_APPEND, PARAM_PROVIDERS_VALUE)
        }.body()

    override suspend fun searchMulti(
        query: String,
        includeAdult: Boolean,
        language: String,
    ): TmdbMultiSearchWrapperRemote = client.get("search/multi") {
        parameter(PARAM_QUERY, query)
        parameter(PARAM_ADULT, includeAdult)
        parameter(PARAM_LANGUAGE, language)
    }.body()

    private companion object {
        const val PARAM_PAGE = "page"
        const val PARAM_QUERY = "query"
        const val PARAM_ADULT = "include_adult"
        const val PARAM_LANGUAGE = "language"
        const val PARAM_APPEND = "append_to_response"
    }
}
